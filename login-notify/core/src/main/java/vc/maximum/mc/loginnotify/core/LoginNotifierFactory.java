package vc.maximum.mc.loginnotify.core;

import java.util.logging.Logger;
import vc.maximum.mc.loginnotify.core.discord.DiscordWebhookLoginNotifier;

public final class LoginNotifierFactory {

  private LoginNotifierFactory() {}

  public static LoginNotifier create(LoginNotifySettings settings, Logger logger) {
    String type = settings.notifierType().trim().toLowerCase();

    return switch (type) {
      case "discord" ->
          createOrNoOp(DiscordWebhookLoginNotifier.fromSettings(settings.discord(), logger));
      case "noop" -> new NoOpLoginNotifier();
      default -> {
        logger.warning("Unknown notifier type '" + type + "'. Notifications are disabled.");
        yield new NoOpLoginNotifier();
      }
    };
  }

  private static LoginNotifier createOrNoOp(LoginNotifier notifier) {
    return notifier == null ? new NoOpLoginNotifier() : notifier;
  }
}
