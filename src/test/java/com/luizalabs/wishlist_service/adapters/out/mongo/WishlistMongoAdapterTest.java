package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistMongoAdapterTest {

    @Mock
    private SpringDataWishlistRepository repository;
    @Mock
    private WishlistMapper mapper;
    @InjectMocks
    private WishlistMongoAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SpringDataWishlistRepository.class);
        mapper = mock(WishlistMapper.class);
        adapter = new WishlistMongoAdapter(repository, mapper);
    }

    @Test
    @DisplayName("save should persist and return mapped domain object")
    void savePersistsAndReturnsDomain() {
        Wishlist domain = Wishlist.create(1L);
        WishlistDocument doc = WishlistDocument.builder().userId(1L).productIds(new LinkedHashSet<>()).build();
        WishlistDocument savedDoc = WishlistDocument.builder().userId(1L).productIds(new LinkedHashSet<>()).build();
        Wishlist mapped = Wishlist.create(1L);
        when(mapper.toDocument(domain)).thenReturn(doc);
        when(repository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(mapped);

        Wishlist result = adapter.save(domain);
        assertThat(result).isEqualTo(mapped);
    }

    @Test
    @DisplayName("remove should delete mapped document and not throw")
    void removeDeletesMappedDocument() throws Exception {
        Wishlist domain = Wishlist.create(1L);
        WishlistDocument doc = WishlistDocument.builder().userId(1L).productIds(new LinkedHashSet<>()).build();
        when(mapper.toDocument(domain)).thenReturn(doc);
        doNothing().when(repository).delete(doc);

        adapter.remove(domain);
        verify(repository).delete(doc);
    }

    @Test
    @DisplayName("remove should wrap and throw exception on error")
    void removeWrapsAndThrowsOnError() {
        Wishlist domain = Wishlist.create(1L);
        WishlistDocument doc = WishlistDocument.builder().userId(1L).productIds(new LinkedHashSet<>()).build();
        when(mapper.toDocument(domain)).thenReturn(doc);
        doThrow(new RuntimeException("fail")).when(repository).delete(doc);

        assertThatThrownBy(() -> adapter.remove(domain))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Error removing wishlist");
    }

    @Test
    @DisplayName("findByUserId should merge multiple docs and return mapped domain")
    void findByUserIdMergesAndReturnsDomain() {
        Long userId = 1L;
        WishlistDocument doc1 = WishlistDocument.builder().userId(userId).productIds(Set.of(1L, 2L)).build();
        WishlistDocument doc2 = WishlistDocument.builder().userId(userId).productIds(Set.of(3L)).build();
        WishlistDocument merged = WishlistDocument.builder().userId(userId).productIds(Set.of(1L, 2L, 3L)).build();
        Wishlist mapped = Wishlist.rehydrate(userId, List.of(1L, 2L, 3L));
        when(repository.findAll()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(any())).thenReturn(mapped);
        when(repository.save(any())).thenReturn(merged);

        Optional<Wishlist> result = adapter.findByUserId(userId);
        assertThat(result).isPresent();
        assertThat(result.get().getProductIds()).containsExactlyInAnyOrder(1L, 2L, 3L);
        verify(repository).delete(doc2);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("findByUserId should return empty if no docs found")
    void findByUserIdReturnsEmptyIfNone() {
        when(repository.findAll()).thenReturn(List.of());
        Optional<Wishlist> result = adapter.findByUserId(1L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findProductForUserWishlist should map and return domain if found")
    void findProductForUserWishlistReturnsDomainIfFound() {
        Long userId = 1L;
        Long productId = 2L;
        WishlistDocument doc = WishlistDocument.builder().userId(userId).productIds(Set.of(productId)).build();
        Wishlist mapped = Wishlist.rehydrate(userId, List.of(productId));
        when(repository.findByUserIdAndProductIdsContaining(userId, productId)).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(mapped);

        Optional<Wishlist> result = adapter.findProductForUserWishlist(userId, productId);
        assertThat(result).isPresent();
        assertThat(result.get().getProductIds()).contains(productId);
    }

    @Test
    @DisplayName("findProductForUserWishlist should return empty if not found")
    void findProductForUserWishlistReturnsEmptyIfNotFound() {
        when(repository.findByUserIdAndProductIdsContaining(1L, 2L)).thenReturn(Optional.empty());
        Optional<Wishlist> result = adapter.findProductForUserWishlist(1L, 2L);
        assertThat(result).isEmpty();
    }
}