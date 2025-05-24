package io.github.pablovns.apiconsumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.github.pablovns.apiconsumer.core.ApiRequestBuilder;
import io.github.pablovns.apiconsumer.core.ApiResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

// Classe principal do consumidor de APIs
public class ApiConsumer {
    private record ParseResult<T>(T data, Exception error) {
    }

    private final Gson gson;
    private final HttpClient httpClient;

    public ApiConsumer() {
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public ApiConsumer(Gson customGson) {
        this.gson = customGson;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    // Método principal para fazer requisições
    public <T> ApiResponse<T> execute(ApiRequestBuilder requestBuilder, Class<T> responseType) {
        try {
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(requestBuilder.getUrl()))
                    .timeout(Duration.ofMillis(requestBuilder.getTimeout()));

            // Adicionar headers
            for (Map.Entry<String, String> header : requestBuilder.getHeaders().entrySet()) {
                httpRequestBuilder.header(header.getKey(), header.getValue());
            }

            // Configurar método HTTP e body
            switch (requestBuilder.getMethod()) {
                case GET:
                    httpRequestBuilder.GET();
                    break;
                case POST:
                    httpRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(
                            requestBuilder.getBody() != null ? requestBuilder.getBody() : ""));
                    break;
                case PUT:
                    httpRequestBuilder.PUT(HttpRequest.BodyPublishers.ofString(
                            requestBuilder.getBody() != null ? requestBuilder.getBody() : ""));
                    break;
                case DELETE:
                    httpRequestBuilder.DELETE();
                    break;
                case PATCH:
                    httpRequestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofString(
                            requestBuilder.getBody() != null ? requestBuilder.getBody() : ""));
                    break;
                case HEAD:
                    httpRequestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
                    break;
                case OPTIONS:
                    httpRequestBuilder.method("OPTIONS", HttpRequest.BodyPublishers.noBody());
                    break;
            }

            HttpRequest httpRequest = httpRequestBuilder.build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            T data = null;
            Exception error = null;

            ParseResult<T> parseResult = parseResponseData(response.body(), responseType);
            data = parseResult.data();
            error = parseResult.error();

            return new ApiResponse<>(
                    response.statusCode(),
                    response.body(),
                    data,
                    error,
                    response.headers().map()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // reinterrompe a thread
            return new ApiResponse<>(0, null, null, e, null);
        } catch (Exception e) {
            return new ApiResponse<>(0, null, null, e, null);
        }

    }

    private <T> ParseResult<T> parseResponseData(String responseBody, Class<T> responseType) {
        T data = null;
        Exception error = null;

        try {
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                if (responseType == String.class) {
                    data = responseType.cast(responseBody);
                } else {
                    data = gson.fromJson(responseBody, responseType);
                }
            }
        } catch (JsonSyntaxException e) {
            error = e;
        }

        return new ParseResult<>(data, error);
    }

    public ApiResponse<String> execute(ApiRequestBuilder requestBuilder) {
        return execute(requestBuilder, String.class);
    }

    // Métodos de conveniência para diferentes tipos de requisição
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

    // Método para lidar com diferentes categorias de resposta
    public <T> void handleResponse(ApiResponse<T> response,
                                   Consumer<T> onSuccess,
                                   Consumer<ApiResponse<T>> onClientError,
                                   Consumer<ApiResponse<T>> onServerError,
                                   Consumer<ApiResponse<T>> onRedirection) {

        switch (response.getCategory()) {
            case SUCCESS:
                if (onSuccess != null && response.getData() != null) {
                    onSuccess.accept(response.getData());
                }
                break;
            case CLIENT_ERROR:
                if (onClientError != null) {
                    onClientError.accept(response);
                }
                break;
            case SERVER_ERROR:
                if (onServerError != null) {
                    onServerError.accept(response);
                }
                break;
            case REDIRECTION:
                if (onRedirection != null) {
                    onRedirection.accept(response);
                }
                break;
        }
    }
}