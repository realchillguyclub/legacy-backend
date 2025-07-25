package server.poptato.external.notion.formatter;

import server.poptato.user.application.event.CreateUserCommentEvent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NotionPayloadFormatter {

    public static Map<String, Object> formatCreateUserCommentPayload(CreateUserCommentEvent event, String databaseId) {
        Map<String, Object> payload = new HashMap<>();

        payload.put("parent", Map.of("database_id", databaseId));

        Map<String, Object> properties = new HashMap<>();

        properties.put("Date", Map.of(
                "date", Map.of("start", LocalDate.now().toString())
        ));

        properties.put("Device", Map.of(
                "select", Map.of("name", event.mobileType())
        ));

        properties.put("Name", Map.of(
                "title", new Object[] {
                    Map.of("text", Map.of("content", event.userName()))
                }
        ));

        properties.put("Content", Map.of(
                "rich_text", new Object[] {
                    Map.of("text", Map.of("content", event.content()))
                }
        ));

        if (event.contactInfo() != null && !event.contactInfo().isBlank()) {
            properties.put("ContactInfo", Map.of(
                    "rich_text", new Object[] {
                        Map.of("text", Map.of("content", event.contactInfo()))
                    }
            ));
        }

        payload.put("properties", properties);
        return payload;
    }
}
