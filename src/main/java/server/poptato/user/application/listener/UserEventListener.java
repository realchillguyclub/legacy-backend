package server.poptato.user.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import server.poptato.external.discord.sender.DiscordSender;
import server.poptato.external.notion.sender.NotionSender;
import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.application.event.CreateUserEvent;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final DiscordSender discordSender;
    private final NotionSender notionSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateUserComment(CreateUserCommentEvent event) {
        discordSender.sendCreateUserComment(event);
        notionSender.sendCreateUserComment(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateUser(CreateUserEvent event) {
        discordSender.sendCreateUser(event);
    }
}
