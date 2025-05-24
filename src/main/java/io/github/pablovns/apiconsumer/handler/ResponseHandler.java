package io.github.pablovns.apiconsumer.handler;

import io.github.pablovns.apiconsumer.core.ApiResponse;

public interface ResponseHandler<T> {
    void handle(ApiResponse<T> response);
}