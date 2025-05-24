package io.github.pablovns.apiconsumer.core;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

// Builder para configurar requisições
public class ApiRequestBuilder {
    private String url;
    private HttpMethod method = HttpMethod.GET;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private int timeout = 30000; // 30 segundos
    private boolean followRedirects = true;

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
    }

    public ApiRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ApiRequestBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public ApiRequestBuilder header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public ApiRequestBuilder headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public ApiRequestBuilder body(String body) {
        this.body = body;
        return this;
    }

    public ApiRequestBuilder jsonBody(Object object, Gson gson) {
        this.body = gson.toJson(object);
        this.headers.put("Content-Type", "application/json");
        return this;
    }

    public ApiRequestBuilder timeout(int timeoutMs) {
        this.timeout = timeoutMs;
        return this;
    }

    public ApiRequestBuilder followRedirects(boolean follow) {
        this.followRedirects = follow;
        return this;
    }

    // Getters
    public String getUrl() { return url; }
    public HttpMethod getMethod() { return method; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
    public int getTimeout() { return timeout; }
    public boolean shouldFollowRedirects() { return followRedirects; }
}