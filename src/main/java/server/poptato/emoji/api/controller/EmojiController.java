package server.poptato.emoji.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.poptato.emoji.application.response.EmojiResponseDto;
import server.poptato.emoji.application.service.EmojiService;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.domain.value.MobileType;

@RestController
@RequestMapping("/emojis")
@RequiredArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    /**
     * 이모지 목록 조회 API.
     *
     * 사용 가능한 이모지 목록을 페이지네이션 형식으로 조회합니다.
     *
     * @param page 요청 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 항목 수 (기본값: 70)
     * @param mobileType 클라이언트의 모바일 타입
     * @return 그룹화된 이모지 목록과 페이징 정보를 포함한 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<EmojiResponseDto>> getCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "70") int size,
            @Parameter(name = "X-Mobile-Type", in = ParameterIn.HEADER,
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"ANDROID", "IOS"}
                    )
            ) MobileType mobileType
    ) {
        EmojiResponseDto response = emojiService.getGroupedEmojis(mobileType, page, size);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
