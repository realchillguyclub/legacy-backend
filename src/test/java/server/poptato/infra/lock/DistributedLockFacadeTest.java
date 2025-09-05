package server.poptato.infra.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.poptato.global.exception.CustomException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * DistributedLockFacade 단위 테스트 (Redis 등 외부 의존성 없이)
 */
@ExtendWith(MockitoExtension.class)
class DistributedLockFacadeTest {

    @Mock
    LettuceLockRepository lettuceLockRepository;

    DistributedLockFacade distributedLockFacade;

    @BeforeEach
    void setUp() {
        distributedLockFacade = new DistributedLockFacade(lettuceLockRepository);
    }

    @Test
    @DisplayName("[SCN-LOCK-001][TC-LOCK-FACADE-001] 성공 흐름: lock→task→unlock 순서 및 반환값")
    void executeWithLock_성공_순서() {
        // given
        String key = "key1";
        String token = "token-1";
        @SuppressWarnings("unchecked")
        Supplier<String> task = mock(Supplier.class);

        when(lettuceLockRepository.lock(eq(key), any(Duration.class))).thenReturn(token);
        when(task.get()).thenReturn("OK");

        // when
        String result = distributedLockFacade.executeWithLock(key, task);

        // then
        assertThat(result).isEqualTo("OK");

        InOrder inOrder = inOrder(lettuceLockRepository, task);
        inOrder.verify(lettuceLockRepository).lock(eq(key), any(Duration.class));
        inOrder.verify(task).get();
        inOrder.verify(lettuceLockRepository).unlock(key, token);
        verifyNoMoreInteractions(lettuceLockRepository, task);
    }

    @Test
    @DisplayName("[SCN-LOCK-001][TC-LOCK-UNIT-002] 락 획득 실패: CustomException, task/unlock 미호출")
    void executeWithLock_락_획득_실패_CustomException_반환() {
        // given
        String key = "key2";
        @SuppressWarnings("unchecked")
        Supplier<String> task = mock(Supplier.class);

        when(lettuceLockRepository.lock(eq(key), any(Duration.class))).thenReturn(null);

        // when
        assertThatThrownBy(() -> distributedLockFacade.executeWithLock(key, task))
                .isInstanceOf(CustomException.class);

        //then
        verify(task, never()).get();
        verify(lettuceLockRepository, never()).unlock(anyString(), anyString());
    }

    @Test
    @DisplayName("[SCN-LOCK-001][TC-LOCK-UNIT-003] task 예외: 예외 전파되더라도 unlock은 반드시 호출")
    void executeWithLock_예외_전파_시에도_unlock_호출() {
        // given
        String key = "key3";
        String token = "token-3";
        @SuppressWarnings("unchecked")
        Supplier<String> task = mock(Supplier.class);

        when(lettuceLockRepository.lock(eq(key), any(Duration.class))).thenReturn(token);
        when(task.get()).thenThrow(new IllegalStateException("boom"));

        // when
        assertThatThrownBy(() -> distributedLockFacade.executeWithLock(key, task))
                .isInstanceOf(IllegalStateException.class);

        //then
        verify(lettuceLockRepository).unlock(key, token);
    }

    @Test
    @DisplayName("[SCN-LOCK-UNIT-004][TC-LOCK-UNIT-004] 동시 경합: 동일 키에서 1개만 성공, 나머지는 CustomException")
    void executeWithLock_concurrentOnlyOneWins() throws Exception {
        // given
        final String key = "key-concurrent";
        final String token = "token-concurrent";

        AtomicBoolean acquired = new AtomicBoolean(false);
        when(lettuceLockRepository.lock(eq(key), any(Duration.class))).thenAnswer(inv -> {
            return acquired.compareAndSet(false, true) ? token : null;
        });
        doAnswer(inv -> {
            acquired.set(false);
            return null;
        }).when(lettuceLockRepository).unlock(eq(key), eq(token));

        // when
        final int threads = 24;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        AtomicInteger taskEntered = new AtomicInteger(0);
        Supplier<String> task = () -> {
            int n = taskEntered.incrementAndGet();
            try {
                Thread.sleep(120);
                return "OK-" + n;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                taskEntered.decrementAndGet();
            }
        };

        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                start.await();
                return distributedLockFacade.executeWithLock(key, task);
            }));
        }

        start.countDown();

        int success = 0;
        int failed = 0;
        for (Future<String> f : futures) {
            try {
                String r = f.get(5, TimeUnit.SECONDS);
                assertThat(r).startsWith("OK-");
                success++;
            } catch (ExecutionException e) {
                assertThat(e.getCause()).isInstanceOf(CustomException.class);
                failed++;
            }
        }

        pool.shutdownNow();

        // then
        assertThat(success).isEqualTo(1);
        assertThat(failed).isEqualTo(threads - 1);

        verify(lettuceLockRepository, times(1)).unlock(key, token);
    }


}
