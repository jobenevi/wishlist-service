package com.luizalabs.wishlist_service.controller;

import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.usecase.AddProductWishlist;
import com.luizalabs.wishlist_service.usecase.RemoveProductWishlist;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/wishlists/{userId}/items")
@RequiredArgsConstructor
public class WishlistController {

    private final AddProductWishlist addProductWishlist;
    private final RemoveProductWishlist removeProductWishlist;

    @PostMapping
    public ResponseEntity<WishlistProductResponse> addProduct(
            @PathVariable final String userId,
            @Valid @RequestBody final WishlistProductRequest requestBody) {

        final var responseBody = addProductWishlist.addProduct(userId, requestBody);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{productId}")
                                .buildAndExpand(requestBody.getProductId())
                                .toUri()
                )
                .body(responseBody);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProduct(
            @PathVariable final String userId,
            @PathVariable final String productId) {
        removeProductWishlist.removeProduct(userId, productId);
        return ResponseEntity.noContent().build();
    }


}