package com.luizalabs.wishlist_service.config;

import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ApiErrorResponse;
import com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException;
import com.luizalabs.wishlist_service.domain.exception.WishlistMaxLimitException;
import com.luizalabs.wishlist_service.domain.exception.WishlistNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionHandlerAdviceTest {
    private final ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();

    @Test
    @DisplayName("handleLimit returns 422 and correct error body")
    void handleLimitReturnsUnprocessableEntity() {
        WishlistMaxLimitException ex = new WishlistMaxLimitException("max reached");
        ResponseEntity<ApiErrorResponse> response = advice.handleLimit(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getError()).isEqualTo("LIMIT_REACHED");
        assertThat(response.getBody().getMessage()).isEqualTo("max reached");
    }

    @Test
    @DisplayName("handleNotFound returns 404 and correct error body")
    void handleNotFoundReturnsNotFound() {
        WishlistNotFoundException ex = new WishlistNotFoundException("not found");
        ResponseEntity<ApiErrorResponse> response = advice.handleNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getError()).isEqualTo("WISHLIST_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("not found");
    }

    @Test
    @DisplayName("handleValidation returns 400 and correct error body with field error")
    void handleValidationReturnsBadRequestWithFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "field", "must not be null");
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<ApiErrorResponse> response = advice.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).contains("field must not be null");
    }

    @Test
    @DisplayName("handleValidation returns 400 and generic message if no field error")
    void handleValidationReturnsBadRequestWithGenericMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(null);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<ApiErrorResponse> response = advice.handleValidation(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).contains("invalid request");
    }

    @Test
    @DisplayName("handleProductNotFound returns 404 and correct error body")
    void handleProductNotFoundReturnsNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("product missing");
        ResponseEntity<ApiErrorResponse> response = advice.handleProductNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getError()).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("product missing");
    }
}
