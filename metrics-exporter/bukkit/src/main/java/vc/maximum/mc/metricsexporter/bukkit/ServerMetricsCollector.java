package vc.maximum.mc.metricsexporter.bukkit;

import org.bukkit.Server;
import vc.maximum.mc.metricsexporter.core.MetricsSnapshot;

/** Collects server metrics using Bukkit API only. */
public interface ServerMetricsCollector {

  MetricsSnapshot collect(Server server);
}
