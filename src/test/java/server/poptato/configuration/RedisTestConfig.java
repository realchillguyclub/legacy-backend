package server.poptato.configuration;

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataRedisTest
@Testcontainers
@ActiveProfiles("unittest")
public abstract class RedisTestConfig {

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", RedisTestContainer.INSTANCE::getHost);
        registry.add("spring.data.redis.port", () -> RedisTestContainer.INSTANCE.getMappedPort(6379));
    }
}


