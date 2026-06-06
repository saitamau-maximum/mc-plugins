package vc.maximum.mc.loginnotify.core.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpJsonPosterTest {

    private HttpServer server;
    private String serverUrl;
    private final AtomicReference<String> receivedBody = new AtomicReference<>();
    private final AtomicInteger responseStatus = new AtomicInteger(204);
    private final AtomicInteger errorStatus = new AtomicInteger();
    private final Logger logger = Logger.getLogger("test");

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/webhook",
                exchange -> {
                    receivedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                    exchange.sendResponseHeaders(responseStatus.get(), -1);
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
    void postsJsonPayloadToWebhook() throws Exception {
        HttpJsonPoster poster = new HttpJsonPoster();

        CompletableFuture<Void> future = poster.post(serverUrl, "{\"ok\":true}", logger, errorStatus::set);
        future.get();

        assertEquals("{\"ok\":true}", receivedBody.get());
        assertEquals(0, errorStatus.get());
    }

    @Test
    void reportsHttpErrorStatus() throws Exception {
        responseStatus.set(500);
        HttpJsonPoster poster = new HttpJsonPoster();

        poster.post(serverUrl, "{}", logger, errorStatus::set).get();

        assertEquals(500, errorStatus.get());
    }

    @Test
    void completesEvenWhenServerIsUnavailable() throws Exception {
        HttpJsonPoster poster = new HttpJsonPoster();

        poster.post("http://127.0.0.1:1/unreachable", "{}", logger, errorStatus::set).get();

        assertEquals(0, errorStatus.get());
    }
}
