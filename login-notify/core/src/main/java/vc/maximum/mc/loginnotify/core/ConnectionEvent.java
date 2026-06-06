package vc.maximum.mc.loginnotify.core;

import java.time.Instant;
import java.util.UUID;

public record ConnectionEvent(
    ConnectionEventType type,
    String playerName,
    UUID playerUuid,
    String playerAddress,
    int onlineCount,
    String serverName,
    Instant occurredAt) {}
