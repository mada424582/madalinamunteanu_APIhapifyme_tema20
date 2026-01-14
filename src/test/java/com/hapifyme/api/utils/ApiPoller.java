package com.hapifyme.api.utils;

import io.restassured.response.Response;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ApiPoller {

    public static String waitForToken(
            TokenSupplier supplier
    ) {
        final String[] tokenHolder = new String[1];

        await()
                .atMost(15, SECONDS)
                .pollInterval(2, SECONDS)
                .until(() -> {
                    String token = supplier.getToken();
                    if (token != null && !token.isEmpty()) {
                        tokenHolder[0] = token;
                        return true;
                    }
                    return false;
                });

        return tokenHolder[0];
    }

    @FunctionalInterface
    public interface TokenSupplier {
        String getToken();
    }
}
