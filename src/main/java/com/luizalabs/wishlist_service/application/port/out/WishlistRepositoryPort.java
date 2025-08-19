package com.luizalabs.wishlist_service.application.port.out;

import com.luizalabs.wishlist_service.domain.model.Wishlist;

import java.util.Optional;

public interface WishlistRepositoryPort {

    Optional<Wishlist> findByUserId(Long userId);
    Wishlist save(Wishlist wishlist);
    Optional<Wishlist> findProductForUserWishlist(Long userId, Long productId);
    void remove(Wishlist wishlist) throws Exception;

}
