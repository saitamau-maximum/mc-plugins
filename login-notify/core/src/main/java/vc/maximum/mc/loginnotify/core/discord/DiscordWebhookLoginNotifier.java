package vc.maximum.mc.loginnotify.core.discord;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import vc.maximum.mc.loginnotify.core.ConnectionEvent;
import vc.maximum.mc.loginnotify.core.DiscordNotifierSettings;
import vc.maximum.mc.loginnotify.core.LoginNotifier;
import vc.maximum.mc.loginnotify.core.http.HttpJsonPoster;

public final class DiscordWebhookLoginNotifier implements LoginNotifier {

    private final String webhookUrl;
    private final DiscordEmbedPayloadBuilder payloadBuilder;
    private final HttpJsonPoster httpJsonPoster;
    private final Logger logger;

    public DiscordWebhookLoginNotifier(
            String webhookUrl,
            DiscordEmbedSettings settings,
            HttpJsonPoster httpJsonPoster,
            Logger logger) {
        this.webhookUrl = webhookUrl;
        this.payloadBuilder = new DiscordEmbedPayloadBuilder(settings);
        this.httpJsonPoster = httpJsonPoster;
        this.logger = logger;
    }

    @Override
    public String id() {
        return "discord";
    }

    @Override
    public CompletableFuture<Void> notify(ConnectionEvent event) {
        return httpJsonPoster.post(
                webhookUrl,
                payloadBuilder.build(event),
                logger,
                statusCode -> logger.warning("Discord webhook returned HTTP " + statusCode));
    }

    public static LoginNotifier fromSettings(DiscordNotifierSettings settings, Logger logger) {
        if (!settings.hasWebhook()) {
            logger.warning("discord.webhook-url is not set. Notifications are disabled.");
            return null;
        }

        DiscordEmbedSettings embedSettings = new DiscordEmbedSettings(
                settings.joinColor(),
                settings.quitColor(),
                settings.includeUuid(),
                settings.includeIp(),
                settings.includeOnlineCount());

        return new DiscordWebhookLoginNotifier(
                settings.webhookUrl(), embedSettings, new HttpJsonPoster(), logger);
    }
}
