package vc.maximum.mc.metricsexporter.paper;

import java.util.List;
import org.bukkit.Server;
import vc.maximum.mc.metricsexporter.bukkit.BukkitServerMetricsCollector;
import vc.maximum.mc.metricsexporter.bukkit.ServerMetricsCollector;
import vc.maximum.mc.metricsexporter.core.MetricsSnapshot;

public final class PaperServerMetricsCollector implements ServerMetricsCollector {

  private final BukkitServerMetricsCollector bukkitCollector = new BukkitServerMetricsCollector();

  @Override
  public MetricsSnapshot collect(Server server) {
    MetricsSnapshot base = bukkitCollector.collect(server);
    double[] tpsValues = server.getTPS();

    return new MetricsSnapshot(
        base.playersOnline(),
        base.playersMax(),
        base.worldsTotal(),
        base.pluginsEnabled(),
        server.getAverageTickTime(),
        List.of(
            new MetricsSnapshot.TpsSample("1m", tpsValues[0]),
            new MetricsSnapshot.TpsSample("5m", tpsValues[1]),
            new MetricsSnapshot.TpsSample("15m", tpsValues[2])),
        base.worldStats());
  }
}
