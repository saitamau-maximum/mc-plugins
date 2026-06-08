package vc.maximum.mc.metricsexporter.core;

public final class KickReasonNormalizer {

  private KickReasonNormalizer() {}

  public static String normalize(String reason) {
    if (reason == null || reason.isBlank()) {
      return "other";
    }

    String normalized = reason.toLowerCase();

    if (normalized.contains("ban")) {
      return "banned";
    }
    if (normalized.contains("timeout") || normalized.contains("timed out")) {
      return "timeout";
    }
    if (normalized.contains("kick")) {
      return "kicked";
    }

    return "other";
  }
}
