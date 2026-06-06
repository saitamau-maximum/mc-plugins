package vc.maximum.mc.loginnotify.core;

public record DiscordNotifierSettings(
        String webhookUrl,
        int joinColor,
        int quitColor,
        boolean includeUuid,
        boolean includeIp,
        boolean includeOnlineCount) {

    public static DiscordNotifierSettings defaults() {
        return new DiscordNotifierSettings("", 5_763_719, 15_548_997, true, true, true);
    }

    public boolean hasWebhook() {
        return webhookUrl != null && !webhookUrl.isBlank();
    }
}
