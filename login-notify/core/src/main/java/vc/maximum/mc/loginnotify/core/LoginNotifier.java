package vc.maximum.mc.loginnotify.core;

import java.util.concurrent.CompletableFuture;

public interface LoginNotifier {

    String id();

    CompletableFuture<Void> notify(ConnectionEvent event);
}
