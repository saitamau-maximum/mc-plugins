package vc.maximum.mc.loginnotify.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import vc.maximum.mc.loginnotify.core.LoginNotificationService;
import vc.maximum.mc.loginnotify.core.LoginNotifier;
import vc.maximum.mc.loginnotify.core.LoginNotifierFactory;
import vc.maximum.mc.loginnotify.core.LoginNotifySettings;

public final class MaximumLoginNotifyPlugin extends JavaPlugin {

  private LoginNotificationService notificationService;

  @Override
  public void onEnable() {
    saveDefaultConfig();

    LoginNotifySettings settings = LoginNotifyConfigMapper.toSettings(getConfig());
    LoginNotifier notifier = LoginNotifierFactory.create(settings, getLogger());

    if ("noop".equals(notifier.id())) {
      if (!"noop".equalsIgnoreCase(settings.notifierType())) {
        getLogger()
            .warning("Login notifications are disabled. Check notifier.type and adapter settings.");
      }
      return;
    }

    notificationService = new LoginNotificationService(notifier);
    String serverName = LoginNotifyConfigMapper.serverName(getConfig());

    getServer()
        .getPluginManager()
        .registerEvents(
            new PlayerConnectionListener(
                this,
                notificationService,
                serverName,
                getConfig().getBoolean("notify-join", true),
                getConfig().getBoolean("notify-quit", false)),
            this);
    getLogger()
        .info("Login notifications enabled via " + notificationService.notifierId() + " adapter.");
  }

  @Override
  public void onDisable() {
    notificationService = null;
  }
}
