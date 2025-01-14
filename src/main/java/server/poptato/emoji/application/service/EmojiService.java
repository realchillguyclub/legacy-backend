package server.poptato.emoji.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import server.poptato.emoji.application.response.EmojiDTO;
import server.poptato.emoji.application.response.EmojiResponseDTO;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;

    public EmojiResponseDTO getGroupedEmojis(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Emoji> emojiPage = emojiRepository.findAllEmojis(pageRequest);

        Map<String, List<EmojiDTO>> groupedEmojis = groupEmojisByGroupName(emojiPage.getContent());

        return new EmojiResponseDTO(groupedEmojis, emojiPage.getTotalPages());
    }

    private Map<String, List<EmojiDTO>> groupEmojisByGroupName(List<Emoji> emojis) {
        return emojis.stream()
                .filter(emoji -> emoji.getGroupName() != null)
                .collect(Collectors.groupingBy(
                        emoji -> emoji.getGroupName().name(),
                        Collectors.mapping(
                                emoji -> new EmojiDTO(emoji.getId(), emoji.getImageUrl()),
                                Collectors.toList()
                        )
                ));
    }
}