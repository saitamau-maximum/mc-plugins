package vc.maximum.mc.metricsexporter.bukkit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import vc.maximum.mc.metricsexporter.core.MetricsSnapshot;

public final class BukkitServerMetricsCollector implements ServerMetricsCollector {

  @Override
  public MetricsSnapshot collect(Server server) {
    List<MetricsSnapshot.WorldStats> worldStats = new ArrayList<>();
    for (World world : server.getWorlds()) {
      worldStats.add(
          new MetricsSnapshot.WorldStats(
              world.getName(),
              world.getEnvironment().name().toLowerCase(),
              world.getEntities().size(),
              world.getLoadedChunks().length));
    }

    int pluginsEnabled = 0;
    for (Plugin plugin : server.getPluginManager().getPlugins()) {
      if (plugin.isEnabled()) {
        pluginsEnabled++;
      }
    }

    return new MetricsSnapshot(
        server.getOnlinePlayers().size(),
        server.getMaxPlayers(),
        server.getWorlds().size(),
        pluginsEnabled,
        0.0,
        List.of(),
        worldStats);
  }
}
