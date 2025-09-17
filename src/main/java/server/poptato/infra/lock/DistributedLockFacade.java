package server.poptato.infra.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.global.exception.CustomException;
import server.poptato.infra.lock.status.LockErrorStatus;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DistributedLockFacade {

    private final LettuceLockRepository lettuceLockRepository;

    public <T> T executeWithLock(final String key, final Supplier<T> task) {
        String token = lettuceLockRepository.lock(key, Duration.ofSeconds(15));
        if (token == null) {
            throw new CustomException(LockErrorStatus._LOCK_ACQUISITION_FAILED);
        }

        try {
            return task.get();
        } finally {
            lettuceLockRepository.unlock(key, token);
        }
    }
}
