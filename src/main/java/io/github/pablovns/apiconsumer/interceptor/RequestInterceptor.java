package io.github.pablovns.apiconsumer.interceptor;


import java.net.http.HttpRequest;

public interface RequestInterceptor {
    HttpRequest.Builder intercept(HttpRequest.Builder builder);
}
