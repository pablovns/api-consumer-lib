package io.github.pablovns.apiconsumer.handler;

import io.github.pablovns.apiconsumer.core.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResponseHandler<T> implements ResponseHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultResponseHandler.class);

    @Override
    public void handle(ApiResponse<T> response) {
        if (response.isSuccess()) {
            logger.info("Success: {}", response.getData());
        } else if (response.isClientError()) {
            logger.warn("Client error: {}", response.getStatusCode());
        } else if (response.isServerError()) {
            logger.error("Server error: {}", response.getStatusCode());
        } else {
            logger.debug("Other response: {}", response.getStatusCode());
        }
    }
}