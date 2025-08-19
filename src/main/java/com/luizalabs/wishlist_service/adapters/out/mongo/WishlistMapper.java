package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
public class WishlistMapper {
    
    public Wishlist toDomain(WishlistDocument document) {
        if (document == null) return null;
        return Wishlist.rehydrate(document.getUserId(), document.getProductIds());
    }

    public WishlistDocument toDocument(Wishlist model) {
        if (model == null) return null;
        return WishlistDocument.builder()
                .userId(model.getUserId())
                .productIds(new LinkedHashSet<>(model.items()))
                .build();
    }

}
