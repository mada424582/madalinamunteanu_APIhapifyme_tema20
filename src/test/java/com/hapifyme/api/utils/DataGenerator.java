package com.hapifyme.api.utils;

import java.util.UUID;

public class DataGenerator {

    // Metodă statică care generează email random
    public static String randomEmail() {
        // Generează un string unic
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return "testuser+" + unique + "@example.com";
    }

    // Opțional: parolă random
    public static String randomPassword() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return "Pass!" + unique;
    }
}
