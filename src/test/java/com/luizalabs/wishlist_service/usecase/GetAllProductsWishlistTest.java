package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.controller.response.ProductResponse;
import com.luizalabs.wishlist_service.document.WishlistDocument;
import com.luizalabs.wishlist_service.exceptions.WishlistNotFoundException;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllProductsWishlistTest {

    @Mock
    private WishlistRepository repository;

    @InjectMocks
    private GetAllProductsWishlist getAllProductsWishlist;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = "user-1";
    }

    @Test
    void getAllProductsReturnsProductResponsesWhenWishlistExists() {
        Set<String> productIds = new LinkedHashSet<>(List.of("prod-1", "prod-2"));
        WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(productIds)
                .build();
        when(repository.findAllProductsByUserId(userId)).thenReturn(Optional.of(doc));

        List<ProductResponse> result = getAllProductsWishlist.getAllProductsByUserId(userId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductResponse::getProductId)
                .containsExactly("prod-1", "prod-2");
    }

    @Test
    void getAllProductsThrowsExceptionWhenWishlistNotFound() {
        when(repository.findAllProductsByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getAllProductsWishlist.getAllProductsByUserId(userId))
                .isInstanceOf(WishlistNotFoundException.class)
                .hasMessageContaining("Wishlist not found for user: " + userId);
    }

    @Test
    void getAllProductsReturnsEmptyListWhenWishlistHasNoProducts() {
        WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(new LinkedHashSet<>())
                .build();
        when(repository.findAllProductsByUserId(userId)).thenReturn(Optional.of(doc));

        List<ProductResponse> result = getAllProductsWishlist.getAllProductsByUserId(userId);

        assertThat(result).isEmpty();
    }
}