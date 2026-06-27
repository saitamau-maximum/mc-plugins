package vc.maximum.mc.metricsexporter.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.prometheus.client.CollectorRegistry;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class MetricsRegistryTest {

  @Test
  void scrapeContainsUpdatedCountersAndGauges() throws Exception {
    CollectorRegistry registry = new CollectorRegistry();
    MetricsSettings settings = new MetricsSettings("test-server", "127.0.0.1", 9225, 15);
    MetricsRegistry metricsRegistry =
        new MetricsRegistry(settings, registry, Instant.parse("2026-01-01T00:00:00Z"));

    metricsRegistry.recordJoin();
    metricsRegistry.recordKick("You are banned");
    metricsRegistry.recordDeath("world");
    metricsRegistry.updateSnapshot(
        new MetricsSnapshot(
            3,
            20,
            2,
            5,
            42.5,
            List.of(
                new MetricsSnapshot.TpsSample("1m", 19.8),
                new MetricsSnapshot.TpsSample("5m", 20.0),
                new MetricsSnapshot.TpsSample("15m", 20.0)),
            List.of(new MetricsSnapshot.WorldStats("world", "normal", 100, 256))));

    String scrape = metricsRegistry.scrape();

    assertTrue(scrape.contains("minecraft_players_online{server=\"test-server\""));
    assertTrue(scrape.contains("minecraft_player_joins_total{server=\"test-server\""));
    assertTrue(scrape.contains("minecraft_player_kicks_total"));
    assertTrue(scrape.contains("reason=\"banned\""));
    assertTrue(
        scrape.contains("minecraft_player_deaths_total{server=\"test-server\",world=\"world\""));
    assertTrue(scrape.contains("minecraft_tps{server=\"test-server\",window=\"1m\""));
    assertTrue(scrape.contains("minecraft_entities"));
    assertTrue(scrape.contains("world=\"world\""));
    assertTrue(scrape.contains("environment=\"normal\""));
    assertTrue(scrape.contains("minecraft_loaded_chunks{server=\"test-server\",world=\"world\""));
  }

  @Test
  void dropsWorldSeriesWhenWorldUnloads() throws Exception {
    CollectorRegistry registry = new CollectorRegistry();
    MetricsSettings settings = new MetricsSettings("test-server", "127.0.0.1", 9225, 15);
    MetricsRegistry metricsRegistry =
        new MetricsRegistry(settings, registry, Instant.parse("2026-01-01T00:00:00Z"));

    metricsRegistry.updateSnapshot(
        new MetricsSnapshot(
            0,
            20,
            2,
            5,
            0.0,
            List.of(),
            List.of(
                new MetricsSnapshot.WorldStats("world", "normal", 10, 16),
                new MetricsSnapshot.WorldStats("world_nether", "nether", 3, 8))));
    assertTrue(metricsRegistry.scrape().contains("world=\"world_nether\""));

    // world_nether unloads: its series must disappear, not linger at a stale value.
    metricsRegistry.updateSnapshot(
        new MetricsSnapshot(
            0,
            20,
            1,
            5,
            0.0,
            List.of(),
            List.of(new MetricsSnapshot.WorldStats("world", "normal", 12, 16))));

    String scrape = metricsRegistry.scrape();
    assertTrue(scrape.contains("world=\"world\""));
    assertFalse(scrape.contains("world_nether"));
  }
}
