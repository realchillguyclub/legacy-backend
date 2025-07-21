package server.poptato.external.discord.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import server.poptato.external.discord.dto.DiscordMessage;

@FeignClient(
        name = "discordCreateUserWebhookClient",
        url = "${discord.create-user-webhook-url}"
)
public interface DiscordCreateUserWebhookClient {

    @PostMapping
    void sendMessage(@RequestBody DiscordMessage message);
}
