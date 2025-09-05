package server.poptato.infra.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static java.util.Collections.*;

@Component
@RequiredArgsConstructor
public class LettuceLockRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public String lock(final String key, final Duration timeout) {
        String namespaced = generateKey(key);
        String token = java.util.UUID.randomUUID().toString();
        Boolean ok = stringRedisTemplate.opsForValue()
                .setIfAbsent(namespaced, token, timeout);
        return Boolean.TRUE.equals(ok) ? token : null;
    }

    public void unlock(final String key, final String token) {
        String namespaced = generateKey(key);
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "  return redis.call('del', KEYS[1]) " +
                        "else " +
                        "  return 0 " +
                        "end";
        stringRedisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                singletonList(namespaced),
                token
        );
    }

    private String generateKey(final String key) {
        return "lock:" + key;
    }
}
