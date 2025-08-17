package com.luizalabs.wishlist_service.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistProductResponse {

    private String userId;
    private Set<String> productIds;
}
