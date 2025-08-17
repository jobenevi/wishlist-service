package com.luizalabs.wishlist_service.controller;

import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.usecase.AddProductWishlist;
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
}