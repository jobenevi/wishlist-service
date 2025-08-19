package com.luizalabs.wishlist_service.adapters.in.web.mapper;

import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ProductResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.WishlistResponse;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class WishlistWebMapperTest {
    private final WishlistWebMapper mapper = new WishlistWebMapper();

    @Test
    @DisplayName("wishlistToResponse should map domain to response correctly")
    void wishlistToResponseMapsCorrectly() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of(2L, 3L));
        WishlistResponse response = mapper.wishlistToResponse(domain);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getProductIds()).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    @DisplayName("wishlistToProductResponse should map first product correctly")
    void wishlistToProductResponseMapsFirstProduct() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of(5L, 6L));
        ProductResponse response = mapper.wishlistToProductResponse(domain);
        assertThat(response.getProductId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("wishlistToProductResponse should throw if product list is empty")
    void wishlistToProductResponseThrowsIfEmpty() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of());
        assertThatThrownBy(() -> mapper.wishlistToProductResponse(domain))
                .isInstanceOf(NoSuchElementException.class);
    }
}