package vc.maximum.mc.loginnotify.core.discord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import vc.maximum.mc.loginnotify.core.TestConnectionEvents;

class DiscordEmbedPayloadBuilderTest {

    private final DiscordEmbedPayloadBuilder builder =
            new DiscordEmbedPayloadBuilder(new DiscordEmbedSettings(5763719, 15548997, true, true, true));

    @Test
    void buildJoinPayloadIncludesPlayerServerAndColor() {
        String payload = builder.build(TestConnectionEvents.joinEvent("Steve"));

        assertTrue(payload.contains("\"title\": \"🟢 Steve joined Test Server\""));
        assertTrue(payload.contains("Steve が Test Server に参加しました"));
        assertTrue(payload.contains("\"color\": 5763719"));
        assertTrue(payload.contains(TestConnectionEvents.PLAYER_UUID.toString()));
        assertTrue(payload.contains("203.0.113.1"));
        assertTrue(payload.contains("\"name\": \"Online\", \"value\": \"3\", \"inline\": true"));
        assertTrue(payload.contains("\"footer\": {\"text\": \"Test Server\"}"));
        assertTrue(payload.contains("\"timestamp\": \"2026-06-06T10:15:30Z\""));
    }

    @Test
    void buildQuitPayloadUsesQuitColor() {
        String payload = builder.build(TestConnectionEvents.quitEvent("Alex"));

        assertTrue(payload.contains("\"title\": \"🔴 Alex left Test Server\""));
        assertTrue(payload.contains("Alex が Test Server から退出しました"));
        assertTrue(payload.contains("\"color\": 15548997"));
        assertTrue(payload.contains("\"name\": \"Online\", \"value\": \"2\", \"inline\": true"));
    }

    @Test
    void buildEscapesSpecialCharactersInPlayerName() {
        String payload = builder.build(TestConnectionEvents.joinEvent("Evil\"Player"));

        assertTrue(payload.contains("Evil\\\"Player"));
    }

    @Test
    void buildOmitsOptionalFieldsWhenDisabled() {
        DiscordEmbedPayloadBuilder minimalBuilder = new DiscordEmbedPayloadBuilder(
                new DiscordEmbedSettings(5763719, 15548997, false, false, false));

        String payload = minimalBuilder.build(TestConnectionEvents.joinEvent("Steve"));

        assertTrue(payload.contains("\"name\": \"Server\""));
        assertTrue(payload.contains("\"name\": \"Time (JST)\""));
        assertEquals(-1, payload.indexOf("\"name\": \"UUID\""));
        assertEquals(-1, payload.indexOf("\"name\": \"IP\""));
        assertEquals(-1, payload.indexOf("\"name\": \"Online\""));
    }
}
