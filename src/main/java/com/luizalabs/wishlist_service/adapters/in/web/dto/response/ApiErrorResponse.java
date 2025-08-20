package com.luizalabs.wishlist_service.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Error response body")
public class ApiErrorResponse {
    private String error;
    private String message;
}

