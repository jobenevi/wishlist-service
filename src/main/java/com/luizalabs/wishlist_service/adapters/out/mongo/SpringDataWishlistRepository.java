package com.luizalabs.wishlist_service.adapters.out.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataWishlistRepository extends MongoRepository<WishlistDocument, String> {

    Optional<WishlistDocument> findByUserId(Long userId);
    Optional<WishlistDocument> findByUserIdAndProductIdsContaining(Long userId, Long productId);

}
