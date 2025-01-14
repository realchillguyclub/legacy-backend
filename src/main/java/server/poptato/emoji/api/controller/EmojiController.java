package server.poptato.emoji.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.poptato.category.application.response.CategoryListResponseDto;
import server.poptato.emoji.application.response.EmojiResponseDTO;
import server.poptato.emoji.application.service.EmojiService;
import server.poptato.global.response.BaseResponse;
import server.poptato.user.resolver.UserId;

@RestController
@RequestMapping("/emojis")
@RequiredArgsConstructor
public class EmojiController {
    private final EmojiService emojiService;
    @GetMapping
    public BaseResponse<EmojiResponseDTO> getCategories(@UserId Long userId,
                                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                                    @RequestParam(value = "size", defaultValue = "70") int size) {
        EmojiResponseDTO response = emojiService.getGroupedEmojis(page, size);
        return new BaseResponse<>(response);
    }
}
