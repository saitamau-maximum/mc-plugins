package vc.maximum.mc.loginnotify.core;

import java.time.Instant;
import java.util.UUID;

public final class TestConnectionEvents {

    public static final UUID PLAYER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final Instant OCCURRED_AT = Instant.parse("2026-06-06T10:15:30Z");

    private TestConnectionEvents() {}

    public static ConnectionEvent joinEvent(String playerName) {
        return event(ConnectionEventType.JOIN, playerName, 3);
    }

    public static ConnectionEvent quitEvent(String playerName) {
        return event(ConnectionEventType.QUIT, playerName, 2);
    }

    private static ConnectionEvent event(ConnectionEventType type, String playerName, int onlineCount) {
        return new ConnectionEvent(
                type,
                playerName,
                PLAYER_UUID,
                "203.0.113.1",
                onlineCount,
                "mc.maximum.vc",
                OCCURRED_AT);
    }
}
