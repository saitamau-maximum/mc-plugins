package vc.maximum.mc.metricsexporter.core;

public record MetricsSettings(
    String serverName, String bindHost, int bindPort, int scrapeIntervalSeconds) {

  public static final String DEFAULT_BIND_HOST = "0.0.0.0";
  public static final int DEFAULT_BIND_PORT = 9225;
  public static final int DEFAULT_SCRAPE_INTERVAL_SECONDS = 15;

  public MetricsSettings {
    if (serverName == null || serverName.isBlank()) {
      throw new IllegalArgumentException("serverName must not be blank");
    }
    if (bindHost == null || bindHost.isBlank()) {
      throw new IllegalArgumentException("bindHost must not be blank");
    }
    if (bindPort < 1 || bindPort > 65535) {
      throw new IllegalArgumentException("bindPort must be between 1 and 65535");
    }
    if (scrapeIntervalSeconds < 1) {
      throw new IllegalArgumentException("scrapeIntervalSeconds must be at least 1");
    }
  }

  public static MetricsSettings defaults() {
    return new MetricsSettings(
        "test-server", DEFAULT_BIND_HOST, DEFAULT_BIND_PORT, DEFAULT_SCRAPE_INTERVAL_SECONDS);
  }
}
