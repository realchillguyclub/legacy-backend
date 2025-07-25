package server.poptato.external.notion.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import server.poptato.external.notion.client.NotionCreateUserCommentClient;
import server.poptato.external.notion.formatter.NotionPayloadFormatter;
import server.poptato.user.application.event.CreateUserCommentEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotionSender {

    private final NotionCreateUserCommentClient notionCreateUserCommentClient;

    @Value("${notion.database-id}")
    private String databaseId;

    @Retryable(
            retryFor = { Exception.class },
            backoff = @Backoff(delay = 2000)
    )
    public void sendCreateUserCommentMessage(CreateUserCommentEvent event) {
        Map<String, Object> payload = NotionPayloadFormatter.formatCreateUserCommentPayload(event, databaseId);
        notionCreateUserCommentClient.sendUserComment(payload);
    }
}
