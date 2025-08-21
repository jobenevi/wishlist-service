package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.domain.model.Wishlist;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
    @Mock
    private MongoTemplate mongoTemplate;
    @InjectMocks
    private WishlistMongoAdapter adapter;

    @Test
    @DisplayName("save should persist and return mapped domain object")
    void savePersistsAndReturnsDomain() {
        Wishlist domain = Wishlist.create(1L);
        WishlistDocument doc = WishlistDocument.builder().userId(1L).productIds(new LinkedHashSet<>()).build();
        WishlistDocument savedDoc = WishlistDocument.builder().userId(1L).productIds(new LinkedHashSet<>()).build();
        Wishlist mapped = Wishlist.create(1L);

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoTemplate.upsert(any(), any(), eq(WishlistDocument.class))).thenReturn(null);
        when(mongoTemplate.findOne(any(), eq(WishlistDocument.class))).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(mapped);

        Wishlist result = adapter.save(domain);
        assertThat(result).isEqualTo(mapped);
        verify(mongoTemplate).upsert(any(), any(), eq(WishlistDocument.class));
        verify(mongoTemplate).findOne(any(), eq(WishlistDocument.class));
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

    @Test
    @DisplayName("remove removes product when document is found")
    void remove_removesProduct_whenDocumentFound() throws Exception {
        Long userId = 1L;
        Long productId = 2L;
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(mongoTemplate.updateFirst(any(), any(), eq(WishlistDocument.class))).thenReturn(updateResult);

        adapter.remove(userId, productId);
        verify(mongoTemplate).updateFirst(
                eq(new Query(Criteria.where("userId").is(userId))),
                eq(new Update().pull("productIds", productId)),
                eq(WishlistDocument.class)
        );
    }

    @Test
    @DisplayName("remove throws Exception when no document is found")
    void remove_throwsException_whenNoDocumentFound() {
        Long userId = 1L;
        Long productId = 2L;
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);
        when(mongoTemplate.updateFirst(any(), any(), eq(WishlistDocument.class))).thenReturn(updateResult);

        assertThatThrownBy(() -> adapter.remove(userId, productId))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Wishlist document not found for userId: " + userId);
    }

    @Test
    @DisplayName("remove throws NullPointerException when updateFirst returns null")
    void remove_throwsNullPointerException_whenResultIsNull() {
        Long userId = 1L;
        Long productId = 2L;
        when(mongoTemplate.updateFirst(any(), any(), eq(WishlistDocument.class))).thenReturn(null);

        assertThatThrownBy(() -> adapter.remove(userId, productId))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("findByUserId returns mapped domain when document exists")
    void findByUserId_returnsMappedDomain_whenDocumentExists() {
        Long userId = 42L;
        WishlistDocument doc = WishlistDocument.builder().userId(userId).productIds(Set.of(10L, 20L)).build();
        Wishlist mapped = Wishlist.rehydrate(userId, List.of(10L, 20L));
        when(repository.findByUserId(userId)).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(mapped);

        Optional<Wishlist> result = adapter.findByUserId(userId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mapped);
    }

    @Test
    @DisplayName("findByUserId returns empty when document does not exist")
    void findByUserId_returnsEmpty_whenDocumentDoesNotExist() {
        Long userId = 99L;
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<Wishlist> result = adapter.findByUserId(userId);

        assertThat(result).isEmpty();
    }
}