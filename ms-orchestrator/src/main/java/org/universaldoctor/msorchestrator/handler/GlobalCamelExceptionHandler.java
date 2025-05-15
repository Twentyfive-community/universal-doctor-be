package org.universaldoctor.msorchestrator.handler;

import exception.TokenRetrievalException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import response.ResponseWrapper;

@RestControllerAdvice
public class GlobalCamelExceptionHandler {

    @ExceptionHandler(HttpOperationFailedException.class)
    public ResponseEntity<String> handleHttpException(HttpOperationFailedException ex) {
        int statusCode = ex.getStatusCode();
        String responseBody = ex.getResponseBody();
        return ResponseEntity.status(statusCode).body(responseBody);
    }

    @ExceptionHandler(CamelExecutionException.class)
    public ResponseEntity<String> handleCamel(CamelExecutionException ex) {
        Throwable realCause = ex.getCause();

        if (realCause instanceof TokenRetrievalException httpEx) {
            return ResponseEntity
                    .status(401)
                    .body(httpEx.getMessage());
        }

        return ResponseEntity
                .status(500)
                .body("Camel not handled error: " + realCause.getMessage());
    }

}

