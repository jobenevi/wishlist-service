package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.document.WishlistDocument;
import com.luizalabs.wishlist_service.exceptions.WishlistMaxLimitException;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AddProductWishlist {

    public static final int MAX_ITEMS = 20;

    private final WishlistRepository repository;

    public WishlistProductResponse addProduct(final String userId,
                                              final WishlistProductRequest request) {
        final var wishlist = repository.findByUserId(userId)
                .orElseGet(() -> WishlistDocument.builder().userId(userId).build());

        if (wishlist.getProductIds().contains(request.getProductId())) {
            return toResponse(wishlist);
        }

        if (wishlist.getProductIds().size() >= MAX_ITEMS) {
            throw new WishlistMaxLimitException("Wishlist limit reached (" + MAX_ITEMS + ")");
        }

        wishlist.getProductIds().add(request.getProductId());
        repository.save(wishlist);
        return toResponse(wishlist);
    }

    private WishlistProductResponse toResponse(final WishlistDocument document) {
        return WishlistProductResponse.builder()
                .userId(document.getUserId())
                .productIds(document.getProductIds())
                .build();
    }
}