package server.poptato.emoji.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.emoji.status.EmojiErrorStatus;
import server.poptato.global.exception.CustomException;

@Component
@RequiredArgsConstructor
public class EmojiValidator {

    private final EmojiRepository emojiRepository;

    /**
     * 특정 이모지가 존재하는지 검증합니다.
     * 존재하지 않을 경우 예외를 발생시킵니다.
     *
     * @param id 검증할 이모지 ID
     * @throws CustomException 존재하지 않는 경우 {@link EmojiErrorStatus#_EMOJI_NOT_EXIST} 예외 발생
     */
    public void checkIsExistEmoji(Long id) {
        emojiRepository.findById(id)
                .orElseThrow(() -> new CustomException(EmojiErrorStatus._EMOJI_NOT_EXIST));
    }

    /**
     * 특정 이모지가 존재하는지 검증하고, 존재하는 경우 해당 이모지를 반환합니다.
     * 존재하지 않을 경우 예외를 발생시킵니다.
     *
     * @param id 검증 및 반환할 이모지 ID
     * @return 존재하는 이모지 객체
     * @throws CustomException 존재하지 않는 경우 {@link EmojiErrorStatus#_EMOJI_NOT_EXIST} 예외 발생
     */
    public Emoji checkIsExistAndReturnEmoji(Long id) {
        return emojiRepository.findById(id)
                .orElseThrow(() -> new CustomException(EmojiErrorStatus._EMOJI_NOT_EXIST));
    }
}
