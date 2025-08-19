package com.luizalabs.wishlist_service.application.service;

import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException;
import com.luizalabs.wishlist_service.domain.exception.WishlistNotFoundException;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WishlistService implements AddProductUseCase,
        RemoveProductUseCase,
        ListProductsUseCase,
        ProductUseCase {

    private final WishlistRepositoryPort repository;

    @Override
    public Wishlist add(final Long userId,
                        final Long productId) {
        final var wishlist = repository.findByUserId(userId)
                .orElseGet(() -> Wishlist.create(userId));
        wishlist.addProduct(productId);
        return repository.save(wishlist);
    }

    @Override
    public void remove(final Long userId,
                       final Long productId) throws Exception {
        var wishlist = repository.findByUserId(userId)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found for user " + userId));
        wishlist.removeProduct(productId);
        repository.remove(wishlist);
    }

    @Override
    public Wishlist get(final Long userId) {
        return repository.findByUserId(userId)
                .orElseGet(() -> Wishlist.create(userId));
    }


    @Override
    public Wishlist getProductForUserWishlist(final Long userId,
                                              final Long productId) {
        return repository.findProductForUserWishlist(userId, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found on wishlist; userId:" + userId));
    }
}
