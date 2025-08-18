package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.exceptions.ProductNotFoundException;
import com.luizalabs.wishlist_service.exceptions.WishlistNotFoundException;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveProductWishlist {

    private final WishlistRepository repository;

    public RemoveProductWishlist(WishlistRepository repository) {
        this.repository = repository;
    }

    public void removeProduct(final String userId,
                              final String productId) {
        final var wishlist = repository.findByUserId(userId)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found for user: " + userId));

        if (!wishlist.getProductIds().remove(productId)) {
            throw new ProductNotFoundException("Product not found in wishlist: " + productId);
        }

        repository.save(wishlist);
    }

}
