package vc.maximum.mc.loginnotify.core.discord;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import vc.maximum.mc.loginnotify.core.ConnectionEvent;
import vc.maximum.mc.loginnotify.core.ConnectionEventType;

public final class DiscordEmbedPayloadBuilder {

    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
    private static final DateTimeFormatter JST_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'JST'").withZone(JST);

    private final DiscordEmbedSettings settings;

    public DiscordEmbedPayloadBuilder(DiscordEmbedSettings settings) {
        this.settings = settings;
    }

    public String build(ConnectionEvent event) {
        int color =
                event.type() == ConnectionEventType.JOIN ? settings.joinColor() : settings.quitColor();
        String title = title(event);
        String description = description(event);

        List<String> fields = new ArrayList<>();

        if (settings.includeUuid()) {
            fields.add(field("UUID", event.playerUuid().toString(), false));
        }

        if (settings.includeIp()) {
            fields.add(field("IP", event.playerAddress(), true));
        }

        if (settings.includeOnlineCount()) {
            fields.add(field("Online", String.valueOf(event.onlineCount()), true));
        }

        fields.add(field("Server", event.serverName(), true));
        fields.add(field("Time (JST)", JST_FORMATTER.format(event.occurredAt()), false));

        return """
                {
                  "embeds": [
                    {
                      "title": %s,
                      "description": %s,
                      "color": %d,
                      "timestamp": "%s",
                      "footer": {"text": %s},
                      "fields": %s
                    }
                  ]
                }
                """
                .formatted(
                        jsonString(title),
                        jsonString(description),
                        color,
                        event.occurredAt().toString(),
                        jsonString(event.serverName()),
                        fieldsJson(fields))
                .replace('\n', ' ')
                .replaceAll(" +", " ");
    }

    private static String title(ConnectionEvent event) {
        return switch (event.type()) {
            case JOIN -> "🟢 " + event.playerName() + " joined " + event.serverName();
            case QUIT -> "🔴 " + event.playerName() + " left " + event.serverName();
        };
    }

    private static String description(ConnectionEvent event) {
        return switch (event.type()) {
            case JOIN -> event.playerName() + " が " + event.serverName() + " に参加しました";
            case QUIT -> event.playerName() + " が " + event.serverName() + " から退出しました";
        };
    }

    private static String field(String name, String value, boolean inline) {
        return fieldJson(name, value == null || value.isBlank() ? "(N/A)" : value, inline);
    }

    private static String fieldJson(String name, String value, boolean inline) {
        return """
                {"name": %s, "value": %s, "inline": %s}
                """
                .formatted(jsonString(name), jsonString(value), inline);
    }

    private static String fieldsJson(List<String> fields) {
        return "[" + String.join(", ", fields) + "]";
    }

    private static String jsonString(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        escaped.append('"');
        return escaped.toString();
    }
}
