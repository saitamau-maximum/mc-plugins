package vc.maximum.mc.loginnotify.core.discord;

public record DiscordEmbedSettings(
    int joinColor,
    int quitColor,
    boolean includeUuid,
    boolean includeIp,
    boolean includeOnlineCount) {}
