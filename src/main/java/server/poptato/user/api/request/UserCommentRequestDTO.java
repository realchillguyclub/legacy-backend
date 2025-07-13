package server.poptato.user.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import server.poptato.user.domain.entity.Comment;

public record UserCommentRequestDTO(
        @NotBlank(message = "의견 내용은 비어 있을 수 없습니다.")
        @Size(max = 800, message = "의견 내용은 800자 이내여야 합니다.")
        String content,

        @Size(max = 100, message = "연락처는 100자 이내여야 합니다.")
        String contactInfo
) {
    public Comment toEntity(Long userId) {
        return Comment.builder()
                .userId(userId)
                .content(content)
                .contactInfo(contactInfo)
                .build();
    }
}
