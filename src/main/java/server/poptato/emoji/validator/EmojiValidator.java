package server.poptato.emoji.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.emoji.exception.EmojiException;
import server.poptato.user.domain.entity.User;

import static server.poptato.emoji.exception.errorcode.EmojiExceptionErrorCode.EMOJI_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class EmojiValidator {
    private final EmojiRepository emojiRepository;

    public void checkIsExistEmoji(Long id){
        emojiRepository.findById(id).orElseThrow(() -> new EmojiException(EMOJI_NOT_EXIST));
    }

    public Emoji checkIsExistAndReturnEmoji(Long id){
        return emojiRepository.findById(id)
                .orElseThrow(() -> new EmojiException(EMOJI_NOT_EXIST));
    }
}
