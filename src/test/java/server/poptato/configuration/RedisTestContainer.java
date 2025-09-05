package server.poptato.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public final class RedisTestContainer {

    private static final Logger log = LoggerFactory.getLogger(RedisTestContainer.class);

    public static final GenericContainer<?> INSTANCE =
            new GenericContainer<>("redis:7.0")
                    .withExposedPorts(6379)
                    .withLogConsumer(new Slf4jLogConsumer(log));

    static {
        INSTANCE.start();
    }

    private RedisTestContainer() {
    }
}
