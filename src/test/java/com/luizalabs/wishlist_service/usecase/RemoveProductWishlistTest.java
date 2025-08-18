package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.document.WishlistDocument;
import com.luizalabs.wishlist_service.exceptions.ProductNotFoundException;
import com.luizalabs.wishlist_service.exceptions.WishlistNotFoundException;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveProductWishlistTest {

    @Mock
    private WishlistRepository repository;

    @InjectMocks
    private RemoveProductWishlist removeProductWishlist;

    @Test
    void removeProductRemovesProductWhenPresent() {
        final String userId = "user-1";
        final String productId = "prod-1";
        final WishlistDocument wishlist = WishlistDocument.builder()
                .userId(userId)
                .productIds(new java.util.LinkedHashSet<>(java.util.Set.of(productId, "prod-2")))
                .build();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));

        removeProductWishlist.removeProduct(userId, productId);

        assertThat(wishlist.getProductIds()).doesNotContain(productId);
        verify(repository).save(wishlist);
    }

    @Test
    void removeProductThrowsWhenWishlistNotFound() {
        final String userId = "user-1";
        final String productId = "prod-1";
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                        removeProductWishlist.removeProduct(userId, productId)
                ).isInstanceOf(WishlistNotFoundException.class)
                .hasMessageContaining("Wishlist not found for user: " + userId);
    }

    @Test
    void removeProductThrowsWhenProductNotInWishlist() {
        final String userId = "user-1";
        final String productId = "prod-1";
        final WishlistDocument wishlist = WishlistDocument.builder()
                .userId(userId)
                .productIds(new java.util.LinkedHashSet<>(java.util.Set.of("prod-2")))
                .build();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));

        assertThatThrownBy(() ->
                        removeProductWishlist.removeProduct(userId, productId)
                ).isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found in wishlist: " + productId);
    }


}