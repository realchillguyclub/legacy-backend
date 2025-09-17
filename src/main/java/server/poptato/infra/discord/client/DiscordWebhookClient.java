package server.poptato.infra.discord.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import server.poptato.infra.discord.dto.DiscordMessage;

import java.net.URI;

@FeignClient(
	name = "discordWebhookClient",
	url = "https://temp-url"
)
public interface DiscordWebhookClient {

    @PostMapping
    void sendMessage(URI url, @RequestBody DiscordMessage message);
}
