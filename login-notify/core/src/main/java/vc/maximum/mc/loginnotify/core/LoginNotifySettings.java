package vc.maximum.mc.loginnotify.core;

public record LoginNotifySettings(String notifierType, DiscordNotifierSettings discord) {

  public LoginNotifySettings {
    notifierType = notifierType == null ? "discord" : notifierType.trim();
    discord = discord == null ? DiscordNotifierSettings.defaults() : discord;
  }
}
