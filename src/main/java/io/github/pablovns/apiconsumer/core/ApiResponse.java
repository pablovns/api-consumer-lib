package io.github.pablovns.apiconsumer.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Classe para encapsular a resposta da API
public class ApiResponse<T> {
    private final int statusCode;
    private final HttpStatusCategory category;
    private final String rawResponse;
    private final T data;
    private final Exception error;
    private final Map<String, List<String>> headers;

    public ApiResponse(int statusCode, String rawResponse, T data,
                       Exception error, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.category = HttpStatusCategory.fromStatusCode(statusCode);
        this.rawResponse = rawResponse;
        this.data = data;
        this.error = error;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
    }

    // Getters
    public int getStatusCode() { return statusCode; }
    public HttpStatusCategory getCategory() { return category; }
    public String getRawResponse() { return rawResponse; }
    public T getData() { return data; }
    public Exception getError() { return error; }
    public Map<String, List<String>> getHeaders() { return headers; }

    // Métodos de conveniência
    public boolean isSuccess() { return category == HttpStatusCategory.SUCCESS; }
    public boolean isClientError() { return category == HttpStatusCategory.CLIENT_ERROR; }
    public boolean isServerError() { return category == HttpStatusCategory.SERVER_ERROR; }
    public boolean isRedirection() { return category == HttpStatusCategory.REDIRECTION; }
    public boolean hasError() { return error != null || !isSuccess(); }

    public String getHeader(String name) {
        List<String> values = headers.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }
}