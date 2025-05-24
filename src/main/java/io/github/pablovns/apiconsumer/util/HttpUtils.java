package io.github.pablovns.apiconsumer.util;

import java.net.HttpURLConnection;
import java.util.Map;

public class HttpUtils {
    public static void applyHeaders(HttpURLConnection connection, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}