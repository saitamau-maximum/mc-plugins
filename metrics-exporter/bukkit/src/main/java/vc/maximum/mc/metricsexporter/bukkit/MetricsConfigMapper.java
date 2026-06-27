package vc.maximum.mc.metricsexporter.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import vc.maximum.mc.metricsexporter.core.MetricsSettings;

public final class MetricsConfigMapper {

  private MetricsConfigMapper() {}

  public static String serverName(FileConfiguration config) {
    return config.getString("server-name", "");
  }

  public static MetricsSettings toSettings(FileConfiguration config, String serverName) {
    return new MetricsSettings(
        serverName,
        config.getString("http.host", MetricsSettings.DEFAULT_BIND_HOST),
        config.getInt("http.port", MetricsSettings.DEFAULT_BIND_PORT),
        config.getInt("scrape-interval-seconds", MetricsSettings.DEFAULT_SCRAPE_INTERVAL_SECONDS));
  }
}
