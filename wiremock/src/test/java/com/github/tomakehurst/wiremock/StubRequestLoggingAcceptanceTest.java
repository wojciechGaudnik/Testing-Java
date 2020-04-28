package com.github.tomakehurst.wiremock;

import com.github.tomakehurst.wiremock.common.Notifier;
import com.github.tomakehurst.wiremock.testsupport.WireMockTestClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StubRequestLoggingAcceptanceTest extends AcceptanceTestBase {

    @Test
    public void logsEventsToNotifierWhenNotDisabled() {
        TestNotifier notifier = new TestNotifier();
        WireMockServer wm = new WireMockServer(wireMockConfig().dynamicPort().notifier(notifier));
        wm.start();
        testClient = new WireMockTestClient(wm.port());

        wm.stubFor(get("/log-me").willReturn(ok("body text")));

        testClient.get("/log-me");
        assertThat(notifier.infoMessages.size(), is(1));
        assertThat(notifier.infoMessages.get(0), allOf(
                containsString("Request received:"),
                containsString("/log-me"),
                containsString("body text")
        ));
    }

    @Test
    public void doesNotLogEventsToNotifierWhenDisabled() {
        TestNotifier notifier = new TestNotifier();
        WireMockServer wm = new WireMockServer(wireMockConfig()
                .dynamicPort()
                .stubRequestLoggingDisabled(true)
                .notifier(notifier));
        wm.start();
        testClient = new WireMockTestClient(wm.port());

        wm.stubFor(get("/log-me").willReturn(ok("body")));

        testClient.get("/log-me");
        assertThat(notifier.infoMessages.size(), is(0));
    }

    public static class TestNotifier implements Notifier {

        final List<String> infoMessages = new ArrayList<>();

        @Override
        public void info(String message) {
            infoMessages.add(message);
        }

        @Override
        public void error(String message) {

        }

        @Override
        public void error(String message, Throwable t) {

        }
    }
}
