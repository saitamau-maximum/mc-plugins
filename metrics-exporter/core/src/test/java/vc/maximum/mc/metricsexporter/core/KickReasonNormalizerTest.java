package vc.maximum.mc.metricsexporter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KickReasonNormalizerTest {

  @ParameterizedTest
  @CsvSource({
    "You are banned from this server!, banned",
    "Connection timed out, timeout",
    "Timed out, timeout",
    "Kicked by an operator, kicked",
    "Disconnected, other",
    "'', other"
  })
  void normalizesReason(String reason, String expected) {
    assertEquals(expected, KickReasonNormalizer.normalize(reason));
  }

  @Test
  void normalizesNullAsOther() {
    assertEquals("other", KickReasonNormalizer.normalize(null));
  }
}
