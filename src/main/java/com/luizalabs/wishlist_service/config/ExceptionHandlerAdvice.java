package com.luizalabs.wishlist_service.config;

import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ApiErrorResponse;
import com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException;
import com.luizalabs.wishlist_service.domain.exception.WishlistMaxLimitException;
import com.luizalabs.wishlist_service.domain.exception.WishlistNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(WishlistMaxLimitException.class)
    public ResponseEntity<ApiErrorResponse> handleLimit(WishlistMaxLimitException ex) {
        return ResponseEntity.unprocessableEntity().body(
                ApiErrorResponse.builder()
                        .error("LIMIT_REACHED")
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(WishlistNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(WishlistNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiErrorResponse.builder()
                        .error("WISHLIST_NOT_FOUND")
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var field = ex.getBindingResult().getFieldError();
        var msg = field != null ? field.getField() + " " + field.getDefaultMessage() : "invalid request";
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                        .error("VALIDATION_ERROR")
                        .message(msg)
                        .build()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiErrorResponse.builder()
                        .error("PRODUCT_NOT_FOUND")
                        .message(ex.getMessage())
                        .build()
        );
    }

}
