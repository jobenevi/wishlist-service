package com.luizalabs.wishlist_service.domain.model;

import com.luizalabs.wishlist_service.domain.exception.WishlistMaxLimitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WishlistTest {

    @Test
    @DisplayName("create should initialize with empty product list")
    void createInitializesEmpty() {
        Wishlist wishlist = Wishlist.create(1L);
        assertThat(wishlist.getUserId()).isEqualTo(1L);
        assertThat(wishlist.getProductIds()).isEmpty();
    }

    @Test
    @DisplayName("rehydrate should initialize with given products and remove duplicates")
    void rehydrateInitializesWithProductsAndRemovesDuplicates() {
        Wishlist wishlist = Wishlist.rehydrate(2L, List.of(3L, 3L, 4L));
        assertThat(wishlist.getUserId()).isEqualTo(2L);
        assertThat(wishlist.getProductIds()).containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    @DisplayName("constructor should throw if userId is null")
    void constructorThrowsIfUserIdNull() {
        assertThatThrownBy(() -> Wishlist.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId is required");
    }

    @Test
    @DisplayName("rehydrate should throw if product list exceeds max size")
    void rehydrateThrowsIfExceedsMaxSize() {
        List<Long> products = new ArrayList<>();
        for (long i = 1; i <= Wishlist.MAX_ITEMS + 1; i++) products.add(i);
        assertThatThrownBy(() -> Wishlist.rehydrate(1L, products))
                .isInstanceOf(WishlistMaxLimitException.class)
                .hasMessageContaining("Wishlist exceeds max size");
    }

    @Test
    @DisplayName("addProduct should add product if not present")
    void addProductAddsIfNotPresent() {
        Wishlist wishlist = Wishlist.create(1L);
        wishlist.addProduct(10L);
        assertThat(wishlist.getProductIds()).contains(10L);
    }

    @Test
    @DisplayName("addProduct should not add duplicate product")
    void addProductDoesNotAddDuplicate() {
        Wishlist wishlist = Wishlist.rehydrate(1L, List.of(10L));
        wishlist.addProduct(10L);
        assertThat(wishlist.getProductIds()).containsExactly(10L);
    }

    @Test
    @DisplayName("addProduct should throw if max size reached")
    void addProductThrowsIfMaxSizeReached() {
        List<Long> products = new ArrayList<>();
        for (long i = 1; i <= Wishlist.MAX_ITEMS; i++) products.add(i);
        Wishlist wishlist = Wishlist.rehydrate(1L, products);
        assertThatThrownBy(() -> wishlist.addProduct(99L))
                .isInstanceOf(WishlistMaxLimitException.class)
                .hasMessageContaining("Wishlist reached max size");
    }

    @Test
    @DisplayName("addProduct should throw if productId is null")
    void addProductThrowsIfProductIdNull() {
        Wishlist wishlist = Wishlist.create(1L);
        assertThatThrownBy(() -> wishlist.addProduct(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productId is required");
    }

    @Test
    @DisplayName("removeProduct should remove product if present")
    void removeProductRemovesIfPresent() {
        Wishlist wishlist = Wishlist.rehydrate(1L, List.of(10L, 20L));
        wishlist.removeProduct(10L);
        assertThat(wishlist.getProductIds()).containsExactly(20L);
    }

    @Test
    @DisplayName("removeProduct should do nothing if product not present")
    void removeProductDoesNothingIfNotPresent() {
        Wishlist wishlist = Wishlist.rehydrate(1L, List.of(10L));
        wishlist.removeProduct(99L);
        assertThat(wishlist.getProductIds()).containsExactly(10L);
    }

    @Test
    @DisplayName("removeProduct should throw if productId is null")
    void removeProductThrowsIfProductIdNull() {
        Wishlist wishlist = Wishlist.create(1L);
        assertThatThrownBy(() -> wishlist.removeProduct(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productId is required");
    }

    @Test
    @DisplayName("items should return unmodifiable copy of productIds")
    void itemsReturnsUnmodifiableCopy() {
        Wishlist wishlist = Wishlist.rehydrate(1L, List.of(1L, 2L));
        List<Long> items = wishlist.items();
        assertThat(items).containsExactlyInAnyOrder(1L, 2L);
        assertThatThrownBy(() -> items.add(3L)).isInstanceOf(UnsupportedOperationException.class);
    }
}
