package server.poptato.emoji.application.response;

import java.util.List;
import java.util.Map;

public record EmojiResponseDto(
        Map<String, List<EmojiDto>> groupEmojis,
        int totalPageCount
) {
}
