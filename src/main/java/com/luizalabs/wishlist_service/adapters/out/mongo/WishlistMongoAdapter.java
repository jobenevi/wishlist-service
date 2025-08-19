package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WishlistMongoAdapter implements WishlistRepositoryPort {
    private final SpringDataWishlistRepository repository;
    private final WishlistMapper mapper;

    @Override
    public Wishlist save(final Wishlist wishlist) {
        final var saved = repository.save(mapper.toDocument(wishlist));
        return mapper.toDomain(saved);
    }

    @Override
    public void remove(final Wishlist wishlist) throws Exception {
        try {
            repository.delete(mapper.toDocument(wishlist));
        } catch (Exception e) {
            throw new Exception("Error removing wishlist: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Wishlist> findByUserId(final Long userId) {
        final List<WishlistDocument> docs = repository.findAll().stream()
            .filter(doc -> doc.getUserId().equals(userId))
            .toList();

        if (docs.isEmpty())
            return Optional.empty();

        final Set<Long> mergedProductIds = docs.stream()
            .flatMap(doc -> doc.getProductIds().stream())
            .collect(Collectors.toSet());
        WishlistDocument merged = docs.getFirst();
        merged.setProductIds(mergedProductIds);
        docs.stream().skip(1).forEach(repository::delete);
        repository.save(merged);
        return Optional.of(mapper.toDomain(merged));
    }

    @Override
    public Optional<Wishlist> findProductForUserWishlist(final Long userId,
                                                         final Long productId) {
        return repository.findByUserIdAndProductIdsContaining(userId, productId).map(mapper::toDomain);
    }

}
