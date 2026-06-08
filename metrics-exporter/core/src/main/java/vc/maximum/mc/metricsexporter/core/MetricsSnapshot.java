package vc.maximum.mc.metricsexporter.core;

import java.util.List;

public record MetricsSnapshot(
    int playersOnline,
    int playersMax,
    int worldsTotal,
    int pluginsEnabled,
    double tickDurationMilliseconds,
    List<TpsSample> tpsSamples,
    List<WorldStats> worldStats) {

  public record TpsSample(String window, double value) {}

  public record WorldStats(String world, String environment, int entities, int loadedChunks) {}
}
