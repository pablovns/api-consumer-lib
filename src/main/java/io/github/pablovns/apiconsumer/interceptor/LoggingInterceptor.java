package io.github.pablovns.apiconsumer.interceptor;

import java.net.http.HttpRequest;

public class LoggingInterceptor implements RequestInterceptor {
    @Override
    public HttpRequest.Builder intercept(HttpRequest.Builder builder) {
        System.out.println("Sending request to: " + builder.build().uri());
        return builder;
    }
}
