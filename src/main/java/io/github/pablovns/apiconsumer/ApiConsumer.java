package io.github.pablovns.apiconsumer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.pablovns.apiconsumer.config.ApiConsumerConfig;
import io.github.pablovns.apiconsumer.config.GsonConfig;
import io.github.pablovns.apiconsumer.core.ApiRequestBuilder;
import io.github.pablovns.apiconsumer.core.ApiResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

public class ApiConsumer {
    private record ParseResult<T>(T data, Exception error) {}

    private final Gson gson;
    private final HttpClient httpClient;
    private final ApiConsumerConfig config;

    public ApiConsumer() {
        this(new ApiConsumerConfig(), GsonConfig.defaultGson());
    }

    public ApiConsumer(ApiConsumerConfig config) {
        this(config, GsonConfig.defaultGson());
    }

    public ApiConsumer(ApiConsumerConfig config, Gson customGson) {
        this.config = config;
        this.gson = customGson;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getDefaultTimeout()))
                .followRedirects(config.isFollowRedirects()
                        ? HttpClient.Redirect.ALWAYS
                        : HttpClient.Redirect.NEVER)
                .build();
    }

    public <T> ApiResponse<T> execute(ApiRequestBuilder requestBuilder, Class<T> responseType) {
        try {
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(requestBuilder.getUrl()))
                    .timeout(Duration.ofMillis(
                            requestBuilder.getTimeout() > 0
                                    ? requestBuilder.getTimeout()
                                    : config.getDefaultTimeout()));

            for (Map.Entry<String, String> header : requestBuilder.getHeaders().entrySet()) {
                httpRequestBuilder.header(header.getKey(), header.getValue());
            }

            switch (requestBuilder.getMethod()) {
                case GET -> httpRequestBuilder.GET();
                case POST -> httpRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(
                        requestBuilder.getBody() != null ? requestBuilder.getBody() : ""));
                case PUT -> httpRequestBuilder.PUT(HttpRequest.BodyPublishers.ofString(
                        requestBuilder.getBody() != null ? requestBuilder.getBody() : ""));
                case DELETE -> httpRequestBuilder.DELETE();
                case PATCH -> httpRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofString(
                        requestBuilder.getBody() != null ? requestBuilder.getBody() : ""));
                case HEAD -> httpRequestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
                case OPTIONS -> httpRequestBuilder.method("OPTIONS", HttpRequest.BodyPublishers.noBody());
            }

            HttpRequest httpRequest = httpRequestBuilder.build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            ParseResult<T> parseResult = parseResponseData(response.body(), responseType);

            return new ApiResponse<>(
                    response.statusCode(),
                    response.body(),
                    parseResult.data(),
                    parseResult.error(),
                    response.headers().map()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ApiResponse<>(0, null, null, e, null);
        } catch (Exception e) {
            return new ApiResponse<>(0, null, null, e, null);
        }
    }

    private <T> ParseResult<T> parseResponseData(String responseBody, Class<T> responseType) {
        try {
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                if (responseType == String.class) {
                    return new ParseResult<>(responseType.cast(responseBody), null);
                } else {
                    return new ParseResult<>(gson.fromJson(responseBody, responseType), null);
                }
            }
        } catch (JsonSyntaxException e) {
            return new ParseResult<>(null, e);
        }
        return new ParseResult<>(null, null);
    }

    public ApiResponse<String> execute(ApiRequestBuilder requestBuilder) {
        return execute(requestBuilder, String.class);
    }

    public <T> ApiResponse<T> get(String url, Class<T> responseType) {
        return execute(new ApiRequestBuilder().url(url).method(ApiRequestBuilder.HttpMethod.GET), responseType);
    }

    public <T> ApiResponse<T> post(String url, Object body, Class<T> responseType) {
        return execute(new ApiRequestBuilder()
                .url(url)
                .method(ApiRequestBuilder.HttpMethod.POST)
                .jsonBody(body, gson), responseType);
    }

    public <T> ApiResponse<T> put(String url, Object body, Class<T> responseType) {
        return execute(new ApiRequestBuilder()
                .url(url)
                .method(ApiRequestBuilder.HttpMethod.PUT)
                .jsonBody(body, gson), responseType);
    }

    public <T> ApiResponse<T> delete(String url, Class<T> responseType) {
        return execute(new ApiRequestBuilder()
                .url(url)
                .method(ApiRequestBuilder.HttpMethod.DELETE), responseType);
    }

    public <T> void handleResponse(ApiResponse<T> response,
                                   Consumer<T> onSuccess,
                                   Consumer<ApiResponse<T>> onClientError,
                                   Consumer<ApiResponse<T>> onServerError,
                                   Consumer<ApiResponse<T>> onRedirection) {

        switch (response.getCategory()) {
            case SUCCESS -> {
                if (onSuccess != null && response.getData() != null) {
                    onSuccess.accept(response.getData());
                }
            }
            case CLIENT_ERROR -> {
                if (onClientError != null) {
                    onClientError.accept(response);
                }
            }
            case SERVER_ERROR -> {
                if (onServerError != null) {
                    onServerError.accept(response);
                }
            }
            case REDIRECTION -> {
                if (onRedirection != null) {
                    onRedirection.accept(response);
                }
            }
        }
    }
}
