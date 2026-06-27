package vc.maximum.mc.metricsexporter.core;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import java.io.IOException;
import java.time.Instant;

public final class MetricsExporterService implements AutoCloseable {

  private final MetricsSettings settings;
  private final MetricsRegistry metricsRegistry;
  private final HTTPServer httpServer;

  public MetricsExporterService(MetricsSettings settings) throws IOException {
    this.settings = settings;
    // Dedicated registry per instance: a plugin reload re-runs onEnable, and reusing the
    // static defaultRegistry would fail on duplicate collector registration the second time.
    CollectorRegistry registry = new CollectorRegistry();
    this.metricsRegistry = new MetricsRegistry(settings, registry, Instant.now());
    DefaultExports.register(registry);
    this.httpServer =
        new HTTPServer.Builder()
            .withHostname(settings.bindHost())
            .withPort(settings.bindPort())
            .withRegistry(registry)
            .withDaemonThreads(false)
            .build();
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
