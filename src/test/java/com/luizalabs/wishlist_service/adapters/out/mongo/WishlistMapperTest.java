package com.luizalabs.wishlist_service.adapters.out.mongo;

import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WishlistMapperTest {

    private final WishlistMapper mapper = new WishlistMapper();

    @Test
    @DisplayName("toDomain should map document to domain correctly")
    void toDomainMapsCorrectly() {
        WishlistDocument doc = WishlistDocument.builder()
                .userId(1L)
                .productIds(new LinkedHashSet<>(Set.of(2L, 3L)))
                .build();
        Wishlist result = mapper.toDomain(doc);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getProductIds()).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    @DisplayName("toDomain should return null if document is null")
    void toDomainReturnsNullIfNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDocument should map domain to document correctly")
    void toDocumentMapsCorrectly() {
        Wishlist domain = Wishlist.rehydrate(1L, List.of(2L, 3L));
        WishlistDocument doc = mapper.toDocument(domain);
        assertThat(doc.getUserId()).isEqualTo(1L);
        assertThat(doc.getProductIds()).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    @DisplayName("toDocument should return null if domain is null")
    void toDocumentReturnsNullIfNull() {
        assertThat(mapper.toDocument(null)).isNull();
    }
}