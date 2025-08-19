package com.luizalabs.wishlist_service.config;

import com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException;
import com.luizalabs.wishlist_service.domain.exception.WishlistMaxLimitException;
import com.luizalabs.wishlist_service.domain.exception.WishlistNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionHandlerAdviceTest {
    private final ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();

    @Test
    @DisplayName("handleLimit returns 422 and correct error body")
    void handleLimitReturnsUnprocessableEntity() {
        WishlistMaxLimitException ex = new WishlistMaxLimitException("max reached");
        ResponseEntity<Map<String, Object>> response = advice.handleLimit(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsEntry("error", "LIMIT_REACHED");
        assertThat(response.getBody()).containsEntry("message", "max reached");
    }

    @Test
    @DisplayName("handleNotFound returns 404 and correct error body")
    void handleNotFoundReturnsNotFound() {
        WishlistNotFoundException ex = new WishlistNotFoundException("not found");
        ResponseEntity<Map<String, Object>> response = advice.handleNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", "WISHLIST_NOT_FOUND");
        assertThat(response.getBody()).containsEntry("message", "not found");
    }

    @Test
    @DisplayName("handleValidation returns 400 and correct error body with field error")
    void handleValidationReturnsBadRequestWithFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "field", "must not be null");
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<Map<String, Object>> response = advice.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "VALIDATION_ERROR");
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().get("message").toString()).contains("field must not be null");
    }

    @Test
    @DisplayName("handleValidation returns 400 and generic message if no field error")
    void handleValidationReturnsBadRequestWithGenericMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(null);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<Map<String, Object>> response = advice.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "VALIDATION_ERROR");
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().get("message").toString()).contains("invalid request");
    }

    @Test
    @DisplayName("handleProductNotFound returns 404 and correct error body")
    void handleProductNotFoundReturnsNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("product missing");
        ResponseEntity<Map<String, Object>> response = advice.handleProductNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", "PRODUCT_NOT_FOUND");
        assertThat(response.getBody()).containsEntry("message", "product missing");
    }
}