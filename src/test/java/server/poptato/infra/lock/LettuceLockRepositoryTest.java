package server.poptato.infra.lock;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import server.poptato.configuration.RedisTestConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@Import(LettuceLockRepository.class)
class LettuceLockRepositoryTest extends RedisTestConfig {

    @Autowired
    LettuceLockRepository lockRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private String ns(String raw) { return "lock:" + raw; }

    @BeforeEach
    void clean() {
        stringRedisTemplate.delete(List.of(ns("u1"), ns("u2"), ns("c1"), ns("ttl")));
    }

    @Test
    @DisplayName("[SCN-LOCK-REDIS-001][TC-LOCK-REDIS-001] 최초 획득: 토큰 반환 & TTL 설정")
    void lock_최초_획득_토큰_반환_TTL_설정() {
        // given
        String key = "u1";
        Duration ttl = Duration.ofMillis(500);

        // when
        String token = lockRepository.lock(key, ttl);
        String v = stringRedisTemplate.opsForValue().get(ns(key));
        Long remainMs = stringRedisTemplate.getExpire(ns(key), TimeUnit.MILLISECONDS);

        //then
        assertThat(token).isNotBlank();
        assertThat(v).isEqualTo(token);
        assertThat(remainMs).isNotNull();
        assertThat(remainMs).isPositive();
        assertThat(remainMs).isLessThanOrEqualTo(ttl.toMillis());
    }

    @Test
    @DisplayName("[SCN-LOCK-REDIS-001][TC-LOCK-REDIS-002] 이미 잠김: null 반환")
    void lock_락걸렸을_때_null_반환() {
        // given
        String key = "u1";
        Duration ttl = Duration.ofSeconds(2);

        // when
        String token1 = lockRepository.lock(key, ttl);
        String token2 = lockRepository.lock(key, ttl);

        // then
        assertThat(token1).isNotBlank();
        assertThat(token2).isNull();
    }

    @Test
    @DisplayName("[SCN-LOCK-REDIS-001][TC-LOCK-REDIS-003] 잘못된 토큰 unlock → 해제되지 않음")
    void unlock_잘못된_토큰_해제_안됨() {
        // given
        String key = "u2";
        Duration ttl = Duration.ofSeconds(2);

        // when, then
        String token = lockRepository.lock(key, ttl);
        assertThat(token).isNotBlank();

        lockRepository.unlock(key, "NOT-MY-TOKEN");

        String again = lockRepository.lock(key, ttl);
        assertThat(again).isNull();

        String v = stringRedisTemplate.opsForValue().get(ns(key));
        assertThat(v).isEqualTo(token);
    }

    @Test
    @DisplayName("[SCN-LOCK-REDIS-001][TC-LOCK-REDIS-004] 동시 경합: 동일 키에서 정확히 1개만 획득")
    void concurrentAcquire_같은_키에_대해_1개만_획득() throws Exception {
        // given
        String key = "c1";
        Duration ttl = Duration.ofSeconds(2);

        int threads = 32;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier barrier = new CyclicBarrier(threads);

        // when
        List<Callable<String>> tasks = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                barrier.await(2, TimeUnit.SECONDS);
                return lockRepository.lock(key, ttl);
            });
        }

        List<Future<String>> futures = pool.invokeAll(tasks, 10, TimeUnit.SECONDS);

        int success = 0;
        int fail = 0;
        for (Future<String> future : futures) {
            try {
                String token = future.get(0, TimeUnit.SECONDS);
                if (token != null && !token.isBlank()) success++;
                else fail++;
            } catch (ExecutionException | TimeoutException | CancellationException e) {
                fail++;
            }
        }

        // then
        pool.shutdown();
        assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();

        assertThat(success).isEqualTo(1);
        assertThat(fail).isEqualTo(threads - 1);

        String stored = stringRedisTemplate.opsForValue().get(ns(key));
        assertThat(stored).isNotBlank();
    }

}
