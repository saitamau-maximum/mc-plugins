package vc.maximum.mc.metricsexporter.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import vc.maximum.mc.metricsexporter.core.MetricsRegistry;

final class PlayerActivityListener implements Listener {

  private final MetricsRegistry metricsRegistry;

  PlayerActivityListener(MetricsRegistry metricsRegistry) {
    this.metricsRegistry = metricsRegistry;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    metricsRegistry.recordJoin();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    metricsRegistry.recordQuit();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerKick(PlayerKickEvent event) {
    metricsRegistry.recordKick(event.getReason());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event) {
    metricsRegistry.recordDeath(event.getEntity().getWorld().getName());
  }
}
