package vc.maximum.mc.loginnotify.core.discord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vc.maximum.mc.loginnotify.core.TestConnectionEvents;
import vc.maximum.mc.loginnotify.core.http.HttpJsonPoster;

class DiscordWebhookLoginNotifierTest {

    private HttpServer server;
    private String serverUrl;
    private final AtomicReference<String> receivedBody = new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/webhook",
                exchange -> {
                    receivedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                });
        server.start();
        serverUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/webhook";
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void notifyPostsDiscordEmbedPayload() throws Exception {
        DiscordWebhookLoginNotifier notifier = new DiscordWebhookLoginNotifier(
                serverUrl,
                new DiscordEmbedSettings(5763719, 15548997, true, true, true),
                new HttpJsonPoster(),
                Logger.getLogger("test"));

        notifier.notify(TestConnectionEvents.joinEvent("Steve")).get();

        String body = receivedBody.get();
        assertTrue(body.contains("Steve joined Test Server"));
        assertEquals("discord", notifier.id());
    }
}
