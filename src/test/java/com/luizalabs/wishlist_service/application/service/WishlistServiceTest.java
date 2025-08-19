package com.luizalabs.wishlist_service.application.service;

import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException;
import com.luizalabs.wishlist_service.domain.exception.WishlistMaxLimitException;
import com.luizalabs.wishlist_service.domain.exception.WishlistNotFoundException;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    private static final int MAX_ITEMS = 20;

    @Mock
    private WishlistRepositoryPort repository;

    @Mock
    private WishlistService service;

    @BeforeEach
    void setUp() {
        repository = mock(WishlistRepositoryPort.class);
        service = new WishlistService(repository);
    }

    @Test
    @DisplayName("add should create wishlist if not exists and add product")
    void addCreatesWishlistIfNotExistsAndAddsProduct() {
        Long userId = 1L;
        Long productId = 2L;
        Wishlist savedWishlist = Wishlist.rehydrate(userId, List.of(productId));
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any(Wishlist.class))).thenReturn(savedWishlist);

        Wishlist result = service.add(userId, productId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getProductIds()).containsExactly(productId);
        verify(repository).save(any(Wishlist.class));
    }

    @Test
    @DisplayName("add should add product to existing wishlist")
    void addAddsProductToExistingWishlist() {
        Long userId = 1L;
        Long productId = 2L;
        Wishlist wishlist = Wishlist.create(userId);
        Wishlist updatedWishlist = Wishlist.rehydrate(userId, List.of(productId));
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));
        when(repository.save(any(Wishlist.class))).thenReturn(updatedWishlist);

        Wishlist result = service.add(userId, productId);

        assertThat(result.getProductIds()).contains(productId);
        verify(repository).save(wishlist);
    }

    @Test
    @DisplayName("add should not add duplicate product")
    void addDoesNotAddDuplicateProduct() {
        Long userId = 1L;
        Long productId = 2L;
        Wishlist wishlist = Wishlist.rehydrate(userId, List.of(productId));
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));
        when(repository.save(any(Wishlist.class))).thenReturn(wishlist);

        Wishlist result = service.add(userId, productId);

        assertThat(result.getProductIds()).containsExactly(productId);
    }

    @Test
    @DisplayName("add should throw if wishlist exceeds max size")
    void addThrowsIfWishlistExceedsMaxSize() {
        Long userId = 1L;
        List<Long> products = java.util.stream.LongStream.rangeClosed(1, MAX_ITEMS)
                .boxed().toList();
        Wishlist wishlist = Wishlist.rehydrate(userId, products);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));

        assertThatThrownBy(() -> service.add(userId, 99L))
                .isInstanceOf(WishlistMaxLimitException.class);
    }

    @Test
    @DisplayName("remove should remove product from wishlist")
    void removeRemovesProductFromWishlist() throws Exception {
        Long userId = 1L;
        Long productId = 2L;
        Wishlist wishlist = Wishlist.rehydrate(userId, List.of(productId));
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));
        doNothing().when(repository).remove(userId, productId);

        service.remove(userId, productId);

        assertThat(wishlist.getProductIds()).doesNotContain(productId);
        verify(repository).remove(userId, productId);
    }

    @Test
    @DisplayName("remove should throw if wishlist not found")
    void removeThrowsIfWishlistNotFound() {
        Long userId = 1L;
        Long productId = 2L;
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remove(userId, productId))
                .isInstanceOf(WishlistNotFoundException.class);
    }

    @Test
    @DisplayName("get should return wishlist if exists")
    void getReturnsWishlistIfExists() {
        Long userId = 1L;
        Wishlist wishlist = Wishlist.create(userId);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wishlist));

        Wishlist result = service.get(userId);

        assertThat(result).isEqualTo(wishlist);
    }

    @Test
    @DisplayName("get should create wishlist if not exists")
    void getCreatesWishlistIfNotExists() {
        Long userId = 1L;
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        Wishlist result = service.get(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getProductIds()).isEmpty();
    }

    @Test
    @DisplayName("getProductForUserWishlist should return wishlist with product")
    void getProductForUserWishlistReturnsWishlistWithProduct() {
        Long userId = 1L;
        Long productId = 2L;
        Wishlist wishlist = Wishlist.rehydrate(userId, List.of(productId));
        when(repository.findProductForUserWishlist(userId, productId)).thenReturn(Optional.of(wishlist));

        Wishlist result = service.getProductForUserWishlist(userId, productId);

        assertThat(result.getProductIds()).contains(productId);
    }

    @Test
    @DisplayName("getProductForUserWishlist should throw if product not found")
    void getProductForUserWishlistThrowsIfProductNotFound() {
        Long userId = 1L;
        Long productId = 2L;
        when(repository.findProductForUserWishlist(userId, productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProductForUserWishlist(userId, productId))
                .isInstanceOf(ProductNotFoundException.class);
    }
}