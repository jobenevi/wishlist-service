package com.luizalabs.wishlist_service.adapters.out.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlists")
public class WishlistDocument {

    @Id
    private String id;
    @Indexed(unique = true)
    private Long userId;
    @Builder.Default
    private Set<Long> productIds = new LinkedHashSet<>();

}
