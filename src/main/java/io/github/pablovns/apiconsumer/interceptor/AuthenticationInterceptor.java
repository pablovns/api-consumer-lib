package io.github.pablovns.apiconsumer.interceptor;

import java.net.http.HttpRequest;

public class AuthenticationInterceptor implements RequestInterceptor {
    private final String token;

    public AuthenticationInterceptor(String token) {
        this.token = token;
    }

    @Override
    public HttpRequest.Builder intercept(HttpRequest.Builder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }
}