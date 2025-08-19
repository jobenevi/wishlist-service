package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WishlistMongoAdapter implements WishlistRepositoryPort {
    private final SpringDataWishlistRepository repository;
    private final WishlistMapper mapper;


    @Override
    public Optional<Wishlist> findByUserId(Long userId) {
        return repository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        final var saved = repository.save(mapper.toDocument(wishlist));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Wishlist> findProductForUserWishlist(Long userId, Long productId) {
        return repository.findProductForUserWishlist(userId, productId).map(mapper::toDomain);
    }
}
