package server.poptato.external.notion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "notionCreateUserCommentClient",
        url = "${notion.api-url}"
)
public interface NotionCreateUserCommentClient {

    @PostMapping("/pages")
    void sendUserComment(@RequestBody Map<String, Object> payload);
}
