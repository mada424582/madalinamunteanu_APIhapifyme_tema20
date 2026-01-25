package com.hapifyme.api.utils;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ApiPollerOLD {

    public static String waitForToken(TokenSupplier supplier) {
        final String[] tokenHolder = new String[1];

        await()
                .atMost(7, SECONDS) // poți crește dacă tokenul apare mai greu
                .pollInterval(5, SECONDS)
                .until(() -> {
                    String token = supplier.getToken(); // aici Lambda-ul returnează token-ul
                    System.out.println("Polling token: " + token); // debug token
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
