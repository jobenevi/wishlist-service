package com.luizalabs.wishlist_service.usecase;

import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.document.WishlistDocument;
import com.luizalabs.wishlist_service.exceptions.WishlistMaxLimitException;
import com.luizalabs.wishlist_service.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductWishlistTest {

    @Mock
    private WishlistRepository repository;

    @InjectMocks
    private AddProductWishlist addProductWishlist;

    private String userId;
    private WishlistProductRequest request;

    @BeforeEach
    void setUp() {
        userId = "user-1";
        request = WishlistProductRequest.builder().productId("prod-1").build();
    }

    @Test
    void addProductToNewWishlistCreatesWishlistAndAddsProduct() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        final WishlistProductResponse response = addProductWishlist.addProduct(userId, request);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getProductIds()).containsExactly("prod-1");
        verify(repository).save(any(WishlistDocument.class));
    }

    @Test
    void addProductToExistingWishlistAddsProduct() {
        final WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(new HashSet<>(List.of("prod-2")))
                .build();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        final WishlistProductResponse response = addProductWishlist.addProduct(userId, request);

        assertThat(response.getProductIds()).containsExactlyInAnyOrder("prod-2", "prod-1");
        verify(repository).save(doc);
    }

    @Test
    void addProductAlreadyInWishlistDoesNotDuplicate() {
        final WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(new HashSet<>(List.of("prod-1")))
                .build();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(doc));

        final WishlistProductResponse response = addProductWishlist.addProduct(userId, request);

        assertThat(response.getProductIds()).containsExactly("prod-1");
        verify(repository, never()).save(any());
    }

    @Test
    void addProductWhenWishlistIsFullThrowsException() {
        final Set<String> fullSet = new HashSet<>();
        for (int i = 0; i < AddProductWishlist.MAX_ITEMS; i++) {
            fullSet.add("prod-" + i);
        }
        final WishlistDocument doc = WishlistDocument.builder()
                .userId(userId)
                .productIds(fullSet)
                .build();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(doc));

        assertThatThrownBy(() -> addProductWishlist.addProduct(userId, WishlistProductRequest.builder().productId("prod-x").build()))
                .isInstanceOf(WishlistMaxLimitException.class)
                .hasMessageContaining("Wishlist limit reached");
    }
}