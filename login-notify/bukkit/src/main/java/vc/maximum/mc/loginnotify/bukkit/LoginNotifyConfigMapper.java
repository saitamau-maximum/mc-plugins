package vc.maximum.mc.loginnotify.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import vc.maximum.mc.loginnotify.core.DiscordNotifierSettings;
import vc.maximum.mc.loginnotify.core.LoginNotifySettings;

public final class LoginNotifyConfigMapper {

  private LoginNotifyConfigMapper() {}

  public static LoginNotifySettings toSettings(FileConfiguration config) {
    return new LoginNotifySettings(
        config.getString("notifier.type", "discord"),
        new DiscordNotifierSettings(
            config.getString("discord.webhook-url", ""),
            config.getInt("discord.embed.join-color", 5_763_719),
            config.getInt("discord.embed.quit-color", 15_548_997),
            config.getBoolean("include-uuid", true),
            config.getBoolean("include-ip", true),
            config.getBoolean("include-online-count", true)));
  }

  public static String serverName(FileConfiguration config) {
    return config.getString("server-name", "");
  }
}
