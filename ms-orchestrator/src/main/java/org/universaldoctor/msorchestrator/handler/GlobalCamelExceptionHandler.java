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

    @ExceptionHandler(CamelExecutionException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleCamel(CamelExecutionException ex) {
        Throwable realCause = ex.getCause();

        if (realCause instanceof HttpOperationFailedException httpEx) {
            return ResponseEntity
                    .status(httpEx.getStatusCode())
                    .body(ResponseWrapper.failure(httpEx.getResponseBody(), httpEx.getStatusCode()));
        }

        if (realCause instanceof TokenRetrievalException tokenEx) {
            return ResponseEntity
                    .status(401)
                    .body(ResponseWrapper.failure(tokenEx.getMessage(), 401));
        }

        return ResponseEntity
                .status(500)
                .body(ResponseWrapper.failure("Camel not handled error: " + realCause.getMessage(), 500));
    }


}

