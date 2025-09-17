package server.poptato.global.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "server.poptato.infra")
public class FeignConfig {

    @Bean
    public RequestInterceptor notionRequestInterceptor(
            @Value("${notion.secret-key}") String secretKey) {

        return requestTemplate -> {
            if (requestTemplate.feignTarget().name().startsWith("notion")) {
                requestTemplate.header("Authorization", "Bearer " + secretKey);
                requestTemplate.header("Notion-Version", "2022-06-28");
                requestTemplate.header("Content-Type", "application/json");
            }
        };
    }
}
