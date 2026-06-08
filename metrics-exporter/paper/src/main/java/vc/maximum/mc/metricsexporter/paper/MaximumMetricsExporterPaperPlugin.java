package vc.maximum.mc.metricsexporter.paper;

import org.bukkit.plugin.java.JavaPlugin;
import vc.maximum.mc.metricsexporter.bukkit.MetricsConfigMapper;
import vc.maximum.mc.metricsexporter.bukkit.MetricsExporterSupport;
import vc.maximum.mc.metricsexporter.core.MetricsSettings;

public final class MaximumMetricsExporterPaperPlugin extends JavaPlugin {

  private MetricsExporterSupport exporterSupport;

  @Override
  public void onEnable() {
    saveDefaultConfig();

    String serverName = MetricsConfigMapper.serverName(getConfig());
    if (serverName.isBlank()) {
      serverName = getServer().getName();
    }
    if (serverName.isBlank()) {
      getLogger()
          .severe(
              "server-name is not configured in config.yml and server.properties has no server-name.");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    MetricsSettings settings = MetricsConfigMapper.toSettings(getConfig(), serverName);
    exporterSupport =
        MetricsExporterSupport.enable(this, settings, new PaperServerMetricsCollector());
  }

  @Override
  public void onDisable() {
    if (exporterSupport != null) {
      exporterSupport.disable();
      exporterSupport = null;
    }
  }
}
