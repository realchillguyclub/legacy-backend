package server.poptato.emoji.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;

public interface JpaEmojiRepository extends EmojiRepository, JpaRepository<Emoji, Long> {
    @Query("SELECT e.imageUrl FROM Emoji e WHERE e.id = :emojiId")
    String findImageUrlById(@Param("emojiId") Long emojiId);
    @Query("SELECT e FROM Emoji e WHERE e.id >= 3")
    Page<Emoji> findAllEmojis(Pageable pageable);
}
