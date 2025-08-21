package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WishlistMongoAdapter implements WishlistRepositoryPort {
    private final SpringDataWishlistRepository repository;
    private final WishlistMapper mapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public Wishlist save(final Wishlist wishlist) {
        WishlistDocument doc = mapper.toDocument(wishlist);
        Query query = new Query(Criteria.where("userId").is(doc.getUserId()));
        Update update = new Update().set("productIds", doc.getProductIds());

        mongoTemplate.upsert(query, update, WishlistDocument.class);

        WishlistDocument saved = mongoTemplate.findOne(query, WishlistDocument.class);
        return mapper.toDomain(saved);
    }

    @Override
    public void remove(final Long userId,
                       final Long productId) throws Exception {
        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update().pull("productIds", productId);
        var result = mongoTemplate.updateFirst(query, update, WishlistDocument.class);
        if (result.getMatchedCount() == 0) {
            throw new Exception("Wishlist document not found for userId: " + userId);
        }
    }

    @Override
    public Optional<Wishlist> findByUserId(final Long userId) {
        return repository.findByUserId(userId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Wishlist> findProductForUserWishlist(final Long userId,
                                                         final Long productId) {
        return repository.findByUserIdAndProductIdsContaining(userId, productId).map(mapper::toDomain);
    }

}
