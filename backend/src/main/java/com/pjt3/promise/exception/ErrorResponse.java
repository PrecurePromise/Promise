package com.pjt3.promise.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponse {

    private int statusCode;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorResponse.builder()
                        .statusCode(e.getStatusCode())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build()
                );
    }
}
