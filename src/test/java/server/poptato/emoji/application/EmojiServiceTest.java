package server.poptato.emoji.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import server.poptato.configuration.ServiceTestConfig;
import server.poptato.emoji.application.response.EmojiDto;
import server.poptato.emoji.application.response.EmojiResponseDto;
import server.poptato.emoji.application.service.EmojiService;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.emoji.domain.value.GroupName;
import server.poptato.global.util.FileUtil;
import server.poptato.user.domain.value.MobileType;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmojiServiceTest extends ServiceTestConfig {

    @InjectMocks
    private EmojiService emojiService;

    @Mock
    private EmojiRepository emojiRepository;

    private static Emoji emoji(Long id, GroupName group, String url) {
        Emoji e = Emoji.builder()
                .imageUrl(url)
                .groupName(group)
                .build();
        ReflectionTestUtils.setField(e, "id", id);
        return e;
    }

    @Test
    @DisplayName("[SCN-SVC-EMOJI-001][TC-SVC-EMOJI-001] 서로 다른 그룹으로 묶이고, url 확장자가 치환되고, totalPages를 그대로 반환한다")
    void getGroupedEmojisSuccessfully() {
        // given
        List<Emoji> emojis = List.of(
                emoji(1L, GroupName.운동, "https://img/1.png"),
                emoji(2L, GroupName.운동, "https://img/2.png"),
                emoji(3L, GroupName.데일리, "https://img/3.jpeg")
        );
        PageRequest pageRequest = PageRequest.of(0, 70);
        Page<Emoji> emojiPage = new PageImpl<>(emojis, pageRequest, 3);
        when(emojiRepository.findAllEmojis(pageRequest)).thenReturn(emojiPage);

        MobileType mobileType = MobileType.IOS;

        try (MockedStatic<FileUtil> mocked = Mockito.mockStatic(FileUtil.class)) {
            mocked.when(() -> FileUtil.changeFileExtension("https://img/1.png", mobileType.getImageUrlExtension()))
                    .thenReturn("https://img/1.svg");
            mocked.when(() -> FileUtil.changeFileExtension("https://img/2.png", mobileType.getImageUrlExtension()))
                    .thenReturn("https://img/2.svg");
            mocked.when(() -> FileUtil.changeFileExtension("https://img/3.jpeg", mobileType.getImageUrlExtension()))
                    .thenReturn("https://img/3.svg");

            // when
            EmojiResponseDto emojiResponseDto = emojiService.getGroupedEmojis(mobileType, 0, 70);

            // then
            verify(emojiRepository, times(1)).findAllEmojis(pageRequest);
            assertThat(emojiResponseDto.totalPageCount()).isEqualTo(1);

            Map<String, List<EmojiDto>> grouped = emojiResponseDto.groupEmojis();
            assertThat(grouped).hasSize(2);

            assertThat(grouped.get("운동"))
                    .extracting(EmojiDto::emojiId, EmojiDto::imageUrl)
                    .containsExactlyInAnyOrder(
                            tuple(1L, "https://img/1.svg"),
                            tuple(2L, "https://img/2.svg")
                    );
            assertThat(grouped.get("데일리"))
                    .extracting(EmojiDto::emojiId, EmojiDto::imageUrl)
                    .containsExactly(
                            tuple(3L, "https://img/3.svg")
                    );
        }
    }
}