package com.luizalabs.wishlist_service.repository;

import com.luizalabs.wishlist_service.document.WishlistDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends MongoRepository<WishlistDocument, String> {
    Optional<WishlistDocument> findByUserId(String userId);
}
