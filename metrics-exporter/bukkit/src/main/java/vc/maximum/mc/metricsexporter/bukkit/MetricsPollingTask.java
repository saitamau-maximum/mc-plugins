package vc.maximum.mc.metricsexporter.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import vc.maximum.mc.metricsexporter.core.MetricsRegistry;

final class MetricsPollingTask implements Runnable {

  private final JavaPlugin plugin;
  private final MetricsRegistry metricsRegistry;
  private final ServerMetricsCollector collector;

  MetricsPollingTask(
      JavaPlugin plugin, MetricsRegistry metricsRegistry, ServerMetricsCollector collector) {
    this.plugin = plugin;
    this.metricsRegistry = metricsRegistry;
    this.collector = collector;
  }

  @Override
  public void run() {
    metricsRegistry.updateSnapshot(collector.collect(plugin.getServer()));
  }
}
