package server.poptato.emoji.application.response;

import java.util.List;
import java.util.Map;

public record EmojiResponseDTO(
        Map<String, List<EmojiDTO>> groupEmojis,
        int totalPageCount
) {
}
