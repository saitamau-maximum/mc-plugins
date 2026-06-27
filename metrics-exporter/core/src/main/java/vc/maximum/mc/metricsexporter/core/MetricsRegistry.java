package vc.maximum.mc.metricsexporter.core;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MetricsRegistry {

  private final String serverName;
  private final CollectorRegistry registry;
  private final Instant startedAt;

  private final Gauge playersOnline;
  private final Gauge playersMax;
  private final Gauge worldsTotal;
  private final Gauge pluginsEnabled;
  private final Gauge tickDurationMilliseconds;
  private final Gauge tps;
  private final Gauge entities;
  private final Gauge loadedChunks;
  private final Gauge uptimeSeconds;

  private final Counter playerJoins;
  private final Counter playerQuits;
  private final Counter playerKicks;
  private final Counter playerDeaths;

  // Label sets currently present on the world-scoped gauges, so updateSnapshot can drop
  // only the series for worlds that disappeared instead of clearing every series.
  private final Set<List<String>> entitiesLabels = new HashSet<>();
  private final Set<List<String>> loadedChunksLabels = new HashSet<>();

  MetricsRegistry(MetricsSettings settings, CollectorRegistry registry, Instant startedAt) {
    this.serverName = settings.serverName();
    this.registry = registry;
    this.startedAt = startedAt;

    playersOnline =
        Gauge.build()
            .name("minecraft_players_online")
            .help("Number of online players")
            .labelNames("server")
            .register(registry);

    playersMax =
        Gauge.build()
            .name("minecraft_players_max")
            .help("Maximum number of players")
            .labelNames("server")
            .register(registry);

    worldsTotal =
        Gauge.build()
            .name("minecraft_worlds_total")
            .help("Number of loaded worlds")
            .labelNames("server")
            .register(registry);

    pluginsEnabled =
        Gauge.build()
            .name("minecraft_plugins_enabled")
            .help("Number of enabled plugins")
            .labelNames("server")
            .register(registry);

    tickDurationMilliseconds =
        Gauge.build()
            .name("minecraft_tick_duration_milliseconds")
            .help("Average tick duration in milliseconds")
            .labelNames("server")
            .register(registry);

    tps =
        Gauge.build()
            .name("minecraft_tps")
            .help("Server ticks per second")
            .labelNames("server", "window")
            .register(registry);

    entities =
        Gauge.build()
            .name("minecraft_entities")
            .help("Number of loaded entities")
            .labelNames("server", "world", "environment")
            .register(registry);

    loadedChunks =
        Gauge.build()
            .name("minecraft_loaded_chunks")
            .help("Number of loaded chunks")
            .labelNames("server", "world")
            .register(registry);

    uptimeSeconds =
        Gauge.build()
            .name("minecraft_uptime_seconds")
            .help("Plugin uptime in seconds")
            .labelNames("server")
            .register(registry);

    playerJoins =
        Counter.build()
            .name("minecraft_player_joins_total")
            .help("Total player join events")
            .labelNames("server")
            .register(registry);

    playerQuits =
        Counter.build()
            .name("minecraft_player_quits_total")
            .help("Total player quit events")
            .labelNames("server")
            .register(registry);

    playerKicks =
        Counter.build()
            .name("minecraft_player_kicks_total")
            .help("Total player kick events")
            .labelNames("server", "reason")
            .register(registry);

    playerDeaths =
        Counter.build()
            .name("minecraft_player_deaths_total")
            .help("Total player death events")
            .labelNames("server", "world")
            .register(registry);
  }

  public void updateSnapshot(MetricsSnapshot snapshot) {
    playersOnline.labels(serverName).set(snapshot.playersOnline());
    playersMax.labels(serverName).set(snapshot.playersMax());
    worldsTotal.labels(serverName).set(snapshot.worldsTotal());
    pluginsEnabled.labels(serverName).set(snapshot.pluginsEnabled());
    tickDurationMilliseconds.labels(serverName).set(snapshot.tickDurationMilliseconds());
    uptimeSeconds.labels(serverName).set(elapsedUptimeSeconds());

    for (MetricsSnapshot.TpsSample sample : snapshot.tpsSamples()) {
      tps.labels(serverName, sample.window()).set(sample.value());
    }

    // Diff update rather than clear() + repopulate: the HTTP scrape reads the
    // CollectorRegistry directly on another thread, so wiping every series first would
    // let a scrape observe empty/partial world stats. Update each series in place and
    // only drop the label series for worlds that are no longer loaded.
    Set<List<String>> nextEntities = new HashSet<>();
    Set<List<String>> nextLoadedChunks = new HashSet<>();
    for (MetricsSnapshot.WorldStats worldStats : snapshot.worldStats()) {
      entities
          .labels(serverName, worldStats.world(), worldStats.environment())
          .set(worldStats.entities());
      loadedChunks.labels(serverName, worldStats.world()).set(worldStats.loadedChunks());
      nextEntities.add(List.of(serverName, worldStats.world(), worldStats.environment()));
      nextLoadedChunks.add(List.of(serverName, worldStats.world()));
    }

    removeStaleSeries(entities, entitiesLabels, nextEntities);
    removeStaleSeries(loadedChunks, loadedChunksLabels, nextLoadedChunks);
  }

  private static void removeStaleSeries(
      Gauge gauge, Set<List<String>> tracked, Set<List<String>> current) {
    tracked.removeIf(
        labels -> {
          boolean stale = !current.contains(labels);
          if (stale) {
            gauge.remove(labels.toArray(new String[0]));
          }
          return stale;
        });
    tracked.addAll(current);
  }

  public void recordJoin() {
    playerJoins.labels(serverName).inc();
  }

  public void recordQuit() {
    playerQuits.labels(serverName).inc();
  }

  public void recordKick(String reason) {
    playerKicks.labels(serverName, KickReasonNormalizer.normalize(reason)).inc();
  }

  public void recordDeath(String world) {
    playerDeaths.labels(serverName, sanitizeWorldLabel(world)).inc();
  }

  public String scrape() throws IOException {
    StringWriter writer = new StringWriter();
    TextFormat.write004(writer, registry.metricFamilySamples());
    return writer.toString();
  }

  CollectorRegistry collectorRegistry() {
    return registry;
  }

  private double elapsedUptimeSeconds() {
    return Math.max(0, Instant.now().getEpochSecond() - startedAt.getEpochSecond());
  }

  private static String sanitizeWorldLabel(String world) {
    if (world == null || world.isBlank()) {
      return "unknown";
    }
    return world.toLowerCase(Locale.ROOT);
  }
}
