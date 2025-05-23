package server.poptato.emoji.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import server.poptato.emoji.application.response.EmojiDto;
import server.poptato.emoji.application.response.EmojiResponseDto;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.global.util.FileUtil;
import server.poptato.user.domain.value.MobileType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;

    /**
     * 그룹화된 이모지 목록을 페이지네이션 형식으로 조회합니다.
     *
     * @param page 요청 페이지 번호
     * @param size 한 페이지당 항목 수
     * @return 그룹화된 이모지 목록 및 페이징 정보를 포함한 응답 객체
     */
    public EmojiResponseDto getGroupedEmojis(MobileType mobileType, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Emoji> emojiPage = emojiRepository.findAllEmojis(pageRequest);

        // 이모지를 그룹 이름별로 그룹화합니다.
        Map<String, List<EmojiDto>> groupedEmojis = groupEmojisByGroupName(emojiPage.getContent(), mobileType);

        return new EmojiResponseDto(groupedEmojis, emojiPage.getTotalPages());
    }

    /**
     * 이모지를 그룹 이름으로 그룹화합니다.
     *
     * @param emojis 그룹화할 이모지 목록
     * @return 그룹화된 이모지 목록 (그룹 이름별로 묶음)
     */
    private Map<String, List<EmojiDto>> groupEmojisByGroupName(List<Emoji> emojis, MobileType mobileType) {
        String extension = mobileType.getImageUrlExtension();
        return emojis.stream()
                // 그룹 이름이 존재하는 이모지만 필터링
                .filter(emoji -> emoji.getGroupName() != null)
                .collect(Collectors.groupingBy(
                        // 그룹 이름을 기준으로 그룹화
                        emoji -> emoji.getGroupName().name(),
                        // 각 그룹의 이모지를 EmojiDTO로 변환 후 리스트로 저장
                        Collectors.mapping(
                                emoji -> new EmojiDto(emoji.getId(), FileUtil.changeFileExtension(emoji.getImageUrl(), extension)),
                                Collectors.toList()
                        )
                ));
    }
}
