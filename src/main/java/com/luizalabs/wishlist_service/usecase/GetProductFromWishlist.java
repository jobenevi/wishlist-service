package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.controller.response.ProductResponse;
import com.luizalabs.wishlist_service.document.WishlistDocument;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.springframework.stereotype.Service;

@Service
public class GetProductFromWishlist {

    private final WishlistRepository wishlistRepository;

    public GetProductFromWishlist(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public ProductResponse getProductFromWishlist(final String userId,
                                                        final String productId) {
        return ProductResponse.builder()
                .productId(getProductByUserAndProductId(userId, productId))
                .build();
    }

    private String getProductByUserAndProductId (final String userId,
                                          final String productId) {
        return wishlistRepository.findAProductByUserIdAndProductId(userId, productId)
                .map(WishlistDocument::getProductIds)
                .flatMap(products -> products.stream()
                        .filter(product -> product.equals(productId))
                        .findFirst())
                .orElseThrow(() -> new RuntimeException("Product not found on wishlist"));
    }

}
