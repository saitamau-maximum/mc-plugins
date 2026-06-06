package vc.maximum.mc.loginnotify.core.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HttpJsonPoster {

  private final HttpClient httpClient;

  public HttpJsonPoster() {
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
  }

  public CompletableFuture<Void> post(
      String url, String payload, Logger logger, IntConsumer onHttpError) {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

    return httpClient
        .sendAsync(request, HttpResponse.BodyHandlers.discarding())
        .thenAccept(
            response -> {
              if (response.statusCode() >= 400) {
                onHttpError.accept(response.statusCode());
              }
            })
        .exceptionally(
            error -> {
              logger.log(Level.WARNING, "Failed to send notification", error);
              return null;
            });
  }
}
