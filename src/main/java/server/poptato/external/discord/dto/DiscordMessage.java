package server.poptato.external.discord.dto;

public record DiscordMessage(
        String content
) {
    public static DiscordMessage of(String content) {
        return new DiscordMessage(content);
    }
}
