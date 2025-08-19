package com.luizalabs.wishlist_service.domain.model;

import com.luizalabs.wishlist_service.domain.exception.WishlistMaxLimitException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@Getter
@Builder
@EqualsAndHashCode(of = "userId")
public class Wishlist {

    public static final int MAX_ITEMS = 20;

    private final Long userId;
    private final List<Long> productIds;

    private Wishlist(Long userId, Collection<Long> initialItems) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        this.userId = userId;
        this.productIds = new ArrayList<>(new HashSet<>(initialItems));
        if (this.productIds.size() > MAX_ITEMS) {
            throw new WishlistMaxLimitException(
                    "Wishlist exceeds max size: " + MAX_ITEMS
            );
        }
    }

    public static Wishlist create(Long userId) {
        return new Wishlist(userId, Set.of());
    }

    public static Wishlist rehydrate(Long userId, Collection<Long> items) {
        return new Wishlist(userId, items);
    }

    public void addProduct(Long productId) {
        requireProduct(productId);
        if (productIds.contains(productId)) return;
        if (productIds.size() >= MAX_ITEMS) {
            throw new WishlistMaxLimitException(
                    "Wishlist reached max size: " + MAX_ITEMS
            );
        }
        productIds.add(productId);
    }

    public void removeProduct(Long productId) {
        requireProduct(productId);
        productIds.remove(productId);
    }

    public List<Long> items() {
        return List.copyOf(productIds);
    }

    private static void requireProduct(Long productId) {
        if (productId == null) throw new IllegalArgumentException("productId is required");
    }
}

