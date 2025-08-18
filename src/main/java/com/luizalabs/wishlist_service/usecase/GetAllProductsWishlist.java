package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.controller.response.ProductResponse;
import com.luizalabs.wishlist_service.exceptions.WishlistNotFoundException;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class GetAllProductsWishlist {

    private final WishlistRepository wishlistRepository;

    public GetAllProductsWishlist(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<ProductResponse> getAllProductsByUserId(final String userId) {
        return wishlistRepository.findAllProductsByUserId(userId)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found for user: " + userId))
                .getProductIds()
                .stream()
                .map(toProductResponse())
                .toList();
    }

    private Function<String, ProductResponse> toProductResponse() {
        return productId -> ProductResponse.builder()
                .productId(productId)
                .build();
    }


}
