package io.github.pablovns.apiconsumer.core;

public enum HttpStatusCategory {
    SUCCESS(200, 299),
    REDIRECTION(300, 399),
    CLIENT_ERROR(400, 499),
    SERVER_ERROR(500, 599),
    INFORMATIONAL(100, 199);

    private final int minCode;
    private final int maxCode;

    HttpStatusCategory(int minCode, int maxCode) {
        this.minCode = minCode;
        this.maxCode = maxCode;
    }

    public static HttpStatusCategory fromStatusCode(int statusCode) {
        for (HttpStatusCategory category : values()) {
            if (statusCode >= category.minCode && statusCode <= category.maxCode) {
                return category;
            }
        }
        return null;
    }

    public boolean matches(int statusCode) {
        return statusCode >= minCode && statusCode <= maxCode;
    }
}