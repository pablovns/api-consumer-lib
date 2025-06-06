package io.github.pablovns.apiconsumer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonConfig {
    private GsonConfig() {
        throw new IllegalStateException("Utility class");
    }

    public static Gson defaultGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
    }
}