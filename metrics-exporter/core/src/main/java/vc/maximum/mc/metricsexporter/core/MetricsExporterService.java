package vc.maximum.mc.metricsexporter.core;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import java.io.IOException;

public final class MetricsExporterService implements AutoCloseable {

  private final MetricsSettings settings;
  private final MetricsRegistry metricsRegistry;
  private final HTTPServer httpServer;

  public MetricsExporterService(MetricsSettings settings) throws IOException {
    this.settings = settings;
    this.metricsRegistry = new MetricsRegistry(settings);
    DefaultExports.initialize();
    this.httpServer = new HTTPServer(settings.bindHost(), settings.bindPort(), false);
  }

  MetricsExporterService(
      MetricsSettings settings, MetricsRegistry metricsRegistry, HTTPServer httpServer) {
    this.settings = settings;
    this.metricsRegistry = metricsRegistry;
    this.httpServer = httpServer;
  }

  public MetricsSettings settings() {
    return settings;
  }

  public MetricsRegistry registry() {
    return metricsRegistry;
  }

  public int bindPort() {
    return httpServer.getPort();
  }

  @Override
  public void close() {
    httpServer.close();
  }
}
