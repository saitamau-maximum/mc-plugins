package vc.maximum.mc.loginnotify.core;

import java.util.concurrent.CompletableFuture;

public final class NoOpLoginNotifier implements LoginNotifier {

  @Override
  public String id() {
    return "noop";
  }

  @Override
  public CompletableFuture<Void> notify(ConnectionEvent event) {
    return CompletableFuture.completedFuture(null);
  }
}
