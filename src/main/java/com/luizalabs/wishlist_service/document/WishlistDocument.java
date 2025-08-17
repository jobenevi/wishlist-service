package com.luizalabs.wishlist_service.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@Document(collection = "wishlists")
public class WishlistDocument {

    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    @Builder.Default
    private Set<String> productIds = new LinkedHashSet<>();

}
