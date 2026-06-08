package vc.maximum.mc.metricsexporter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MetricsSettingsTest {

  @Test
  void defaultsAreValid() {
    MetricsSettings settings = MetricsSettings.defaults();

    assertEquals("test-server", settings.serverName());
    assertEquals("0.0.0.0", settings.bindHost());
    assertEquals(9225, settings.bindPort());
    assertEquals(15, settings.scrapeIntervalSeconds());
  }

  @Test
  void rejectsBlankServerName() {
    assertThrows(
        IllegalArgumentException.class, () -> new MetricsSettings(" ", "0.0.0.0", 9225, 15));
  }

  @Test
  void rejectsInvalidPort() {
    assertThrows(
        IllegalArgumentException.class, () -> new MetricsSettings("test-server", "0.0.0.0", 0, 15));
  }

  @Test
  void rejectsInvalidScrapeInterval() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MetricsSettings("test-server", "0.0.0.0", 9225, 0));
  }
}
