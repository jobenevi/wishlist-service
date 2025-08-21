package com.luizalabs.wishlist_service.adapters.in.web.mapper;

import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ProductResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.WishlistResponse;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.springframework.stereotype.Component;

@Component
public class WishlistWebMapper {

    public WishlistResponse wishlistToResponse(final Wishlist domain) {
        return WishlistResponse.builder()
                .userId(domain.getUserId())
                .productIds(domain.getProductIds())
                .build();
    }

    public ProductResponse wishlistToProductResponse(final Wishlist domain, final Long productId) {
        if (!domain.getProductIds().contains(productId)) {
            throw new IllegalArgumentException("Product " + productId + " not found in wishlist for user " + domain.getUserId());
        }
        return ProductResponse.builder()
                .productId(productId)
                .build();
    }

    public ProductResponse productIdToProductResponse(final Long productId) {
        return ProductResponse.builder()
                .productId(productId)
                .build();
    }
}