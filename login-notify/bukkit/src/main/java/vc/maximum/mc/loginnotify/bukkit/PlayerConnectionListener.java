package vc.maximum.mc.loginnotify.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import vc.maximum.mc.loginnotify.core.ConnectionEvent;
import vc.maximum.mc.loginnotify.core.LoginNotificationService;

final class PlayerConnectionListener implements Listener {

  private final JavaPlugin plugin;
  private final LoginNotificationService notificationService;
  private final String serverName;
  private final boolean notifyJoin;
  private final boolean notifyQuit;

  PlayerConnectionListener(
      JavaPlugin plugin,
      LoginNotificationService notificationService,
      String serverName,
      boolean notifyJoin,
      boolean notifyQuit) {
    this.plugin = plugin;
    this.notificationService = notificationService;
    this.serverName = serverName;
    this.notifyJoin = notifyJoin;
    this.notifyQuit = notifyQuit;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (!notifyJoin) {
      return;
    }

    dispatchAsync(BukkitConnectionEventFactory.fromJoin(serverName, event.getPlayer()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (!notifyQuit) {
      return;
    }

    dispatchAsync(BukkitConnectionEventFactory.fromQuit(serverName, event.getPlayer()));
  }

  private void dispatchAsync(ConnectionEvent connectionEvent) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(plugin, () -> notificationService.notify(connectionEvent));
  }
}
