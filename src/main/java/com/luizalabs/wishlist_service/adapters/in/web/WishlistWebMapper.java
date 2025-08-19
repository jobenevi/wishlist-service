package com.luizalabs.wishlist_service.adapters.in.web;

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

    public ProductResponse wishlistToProductResponse(final Wishlist domain) {
        return ProductResponse.builder()
                .productId(domain.getProductIds().getFirst())
                .build();
    }
}