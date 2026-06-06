package vc.maximum.mc.loginnotify.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import vc.maximum.mc.loginnotify.core.discord.DiscordWebhookLoginNotifier;

class LoginNotifierFactoryTest {

  private static final Logger LOGGER = Logger.getLogger("test");

  @Test
  void createsNoOpNotifierWhenTypeIsNoop() {
    LoginNotifySettings settings =
        new LoginNotifySettings("noop", DiscordNotifierSettings.defaults());

    LoginNotifier notifier = LoginNotifierFactory.create(settings, LOGGER);

    assertEquals("noop", notifier.id());
  }

  @Test
  void createsDiscordNotifierWhenWebhookIsConfigured() {
    LoginNotifySettings settings =
        new LoginNotifySettings(
            "discord",
            new DiscordNotifierSettings(
                "https://discord.example/webhook", 5763719, 15548997, true, true, true));

    LoginNotifier notifier = LoginNotifierFactory.create(settings, LOGGER);

    assertInstanceOf(DiscordWebhookLoginNotifier.class, notifier);
    assertEquals("discord", notifier.id());
  }

  @Test
  void fallsBackToNoOpWhenDiscordWebhookIsMissing() {
    LoginNotifySettings settings =
        new LoginNotifySettings("discord", DiscordNotifierSettings.defaults());

    LoginNotifier notifier = LoginNotifierFactory.create(settings, LOGGER);

    assertEquals("noop", notifier.id());
  }

  @Test
  void fallsBackToNoOpForUnknownType() {
    LoginNotifySettings settings =
        new LoginNotifySettings("slack", DiscordNotifierSettings.defaults());

    LoginNotifier notifier = LoginNotifierFactory.create(settings, LOGGER);

    assertEquals("noop", notifier.id());
  }

  @Test
  void discordFromSettingsReturnsNullWhenWebhookIsMissing() {
    assertNull(
        DiscordWebhookLoginNotifier.fromSettings(DiscordNotifierSettings.defaults(), LOGGER));
  }
}
