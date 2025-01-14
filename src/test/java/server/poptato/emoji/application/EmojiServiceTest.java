package server.poptato.emoji.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.poptato.emoji.application.response.EmojiDTO;
import server.poptato.emoji.application.response.EmojiResponseDTO;
import server.poptato.emoji.application.service.EmojiService;
import server.poptato.emoji.domain.repository.EmojiRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EmojiServiceTest {
    @Autowired
    EmojiRepository emojiRepository;
    @Autowired
    EmojiService emojiService;

    @DisplayName("이모지 리스트 조회시, 그룹별로 그룹핑되어 조회된다.")
    @Test
    void getEmojiList_Success() {
        //given
        int page = 0;
        int size = 8;

        //when
        EmojiResponseDTO responseDTO = emojiService.getGroupedEmojis(page, size);
        Map<String, List<EmojiDTO>> groupEmojis = responseDTO.groupEmojis();

        List<EmojiDTO> emojiList = groupEmojis.get("생산성");

        //then
        assertThat(emojiList.size()).isEqualTo(1);
        assertThat(emojiList.get(0).imageUrl()).isEqualTo("https://example.com/productive-book1.png");
    }
}
