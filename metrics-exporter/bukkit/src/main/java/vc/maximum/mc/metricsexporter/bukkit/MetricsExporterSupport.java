package vc.maximum.mc.metricsexporter.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import vc.maximum.mc.metricsexporter.core.MetricsExporterService;
import vc.maximum.mc.metricsexporter.core.MetricsRegistry;
import vc.maximum.mc.metricsexporter.core.MetricsSettings;

public final class MetricsExporterSupport {

  private MetricsExporterService exporterService;
  private BukkitTask pollingTask;

  private MetricsExporterSupport() {}

  public static MetricsExporterSupport enable(
      JavaPlugin plugin, MetricsSettings settings, ServerMetricsCollector collector) {
    MetricsExporterSupport support = new MetricsExporterSupport();

    try {
      support.exporterService = new MetricsExporterService(settings);
    } catch (Exception exception) {
      plugin.getLogger().severe("Failed to start metrics HTTP server: " + exception.getMessage());
      plugin.getServer().getPluginManager().disablePlugin(plugin);
      return support;
    }

    MetricsRegistry registry = support.exporterService.registry();

    plugin
        .getServer()
        .getPluginManager()
        .registerEvents(new PlayerActivityListener(registry), plugin);

    long intervalTicks = Math.max(1L, settings.scrapeIntervalSeconds() * 20L);
    MetricsPollingTask pollingTask = new MetricsPollingTask(plugin, registry, collector);
    // Collect on the main thread: Bukkit world/entity/chunk APIs are not thread-safe.
    // The 0-tick initial delay records the first snapshot on the next tick.
    support.pollingTask =
        plugin.getServer().getScheduler().runTaskTimer(plugin, pollingTask, 0L, intervalTicks);

    plugin
        .getLogger()
        .info(
            "Metrics exporter listening on "
                + settings.bindHost()
                + ":"
                + support.exporterService.bindPort());

    return support;
  }

  public void disable() {
    if (pollingTask != null) {
      pollingTask.cancel();
      pollingTask = null;
    }

    if (exporterService != null) {
      exporterService.close();
      exporterService = null;
    }
  }
}
