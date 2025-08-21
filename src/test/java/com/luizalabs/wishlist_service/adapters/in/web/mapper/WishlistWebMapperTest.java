package com.luizalabs.wishlist_service.adapters.in.web.mapper;

import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ProductResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.WishlistResponse;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        ProductResponse response = mapper.wishlistToProductResponse(domain, 5L);
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isNotNull();
        assertThat(response.getProductId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("wishlistToProductResponse returns ProductResponse when product is in wishlist")
    void wishlistToProductResponse_returnsProductResponse_whenProductInWishlist() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of(5L, 6L));
        ProductResponse response = mapper.wishlistToProductResponse(domain, 6L);
        assertThat(response.getProductId()).isEqualTo(6L);
    }

    @Test
    @DisplayName("wishlistToProductResponse throws when product is not in wishlist")
    void wishlistToProductResponse_throws_whenProductNotInWishlist() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of(5L, 6L));
        assertThatThrownBy(() -> mapper.wishlistToProductResponse(domain, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product 99 not found in wishlist for user 1");
    }

    @Test
    @DisplayName("wishlistToProductResponse throws when wishlist is empty")
    void wishlistToProductResponse_throws_whenWishlistIsEmpty() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of());
        assertThatThrownBy(() -> mapper.wishlistToProductResponse(domain, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product 1 not found in wishlist for user 1");
    }

    @Test
    @DisplayName("productIdToProductResponse maps productId correctly")
    void productIdToProductResponse_mapsProductIdCorrectly() {
        Long productId = 123L;
        ProductResponse response = mapper.productIdToProductResponse(productId);
        assertThat(response.getProductId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("productIdToProductResponse handles null productId")
    void productIdToProductResponse_handlesNullProductId() {
        ProductResponse response = mapper.productIdToProductResponse(null);
        assertThat(response.getProductId()).isNull();
    }
}
