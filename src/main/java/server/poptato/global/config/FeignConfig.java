package server.poptato.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "server.poptato.external.oauth.kakao")
public class FeignConfig {
}
