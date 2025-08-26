package server.poptato.emoji.infra;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import server.poptato.configuration.RepositoryTestConfig;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.value.GroupName;

class JpaEmojiRepositoryTest extends RepositoryTestConfig {

    @Autowired
    private JpaEmojiRepository jpaEmojiRepository;

    private Emoji persist(String url, GroupName group) {
        Emoji emoji = Emoji.builder()
                .imageUrl(url)
                .groupName(group)
                .build();
        tem.persistAndFlush(emoji);
        tem.clear();
        return emoji;
    }

    @Test
    @DisplayName("[SCN-EMOJI-001][TC-REP-EMOJI-001] emoji id별로 imageUrl을 조회할 수 있다.")
    void findImageUrlById_Url또는null_반환() {
        //given
        Emoji saved = persist("https://test/test.png", GroupName.데일리);

        //when
        String url = jpaEmojiRepository.findImageUrlById(saved.getId());
        String none = jpaEmojiRepository.findImageUrlById(999_999L);

        //then
        Assertions.assertThat(url).isEqualTo("https://test/test.png");
        Assertions.assertThat(none).isNull();
    }

}