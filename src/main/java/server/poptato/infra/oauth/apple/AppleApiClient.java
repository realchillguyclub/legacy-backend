package server.poptato.infra.oauth.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "appleApiClient", url = "https://appleid.apple.com")
public interface AppleApiClient {

    @GetMapping(value = "/auth/keys", consumes = "application/json")
    String getApplePublicKeys();
}
