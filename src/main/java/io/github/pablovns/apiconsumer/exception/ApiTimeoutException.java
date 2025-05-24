package io.github.pablovns.apiconsumer.exception;

public class ApiTimeoutException extends ApiException {
    public ApiTimeoutException(String message) {
        super(message);
    }
}
