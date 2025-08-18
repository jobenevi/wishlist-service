package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.controller.response.ProductResponse;
import com.luizalabs.wishlist_service.document.WishlistDocument;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetProductFromWishlistTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private GetProductFromWishlist getProductFromWishlist;

    @BeforeEach
    void setUp() {
        wishlistRepository = Mockito.mock(WishlistRepository.class);
        getProductFromWishlist = new GetProductFromWishlist(wishlistRepository);
    }

    @Test
    void getProductReturnsProductResponseWhenProductExistsInWishlist() {
        String userId = "user-1";
        String productId = "prod-1";
        Set<String> productIds = new LinkedHashSet<>();
        productIds.add(productId);
        WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(productIds)
                .build();

        Mockito.when(wishlistRepository.findAProductByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.of(doc));

        ProductResponse response = getProductFromWishlist.getProductFromWishlist(userId, productId);

        assertThat(response.getProductId()).isEqualTo(productId);
    }

    @Test
    void getProductThrowsExceptionWhenProductNotInWishlist() {
        String userId = "user-1";
        String productId = "prod-2";
        Set<String> productIds = new LinkedHashSet<>();
        productIds.add("prod-1");
        WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(productIds)
                .build();

        Mockito.when(wishlistRepository.findAProductByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProductFromWishlist.getProductFromWishlist(userId, productId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found on wishlist");
    }

    @Test
    void getProductThrowsExceptionWhenWishlistNotFound() {
        String userId = "user-1";
        String productId = "prod-1";

        Mockito.when(wishlistRepository.findAProductByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProductFromWishlist.getProductFromWishlist(userId, productId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found on wishlist");
    }
}