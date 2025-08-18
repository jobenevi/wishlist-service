package com.luizalabs.wishlist_service.controller;

import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.ProductResponse;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.exceptions.ProductNotFoundException;
import com.luizalabs.wishlist_service.exceptions.WishlistNotFoundException;
import com.luizalabs.wishlist_service.usecase.AddProductWishlist;
import com.luizalabs.wishlist_service.usecase.GetAllProductsWishlist;
import com.luizalabs.wishlist_service.usecase.RemoveProductWishlist;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/wishlists/{userId}")
@RequiredArgsConstructor
public class WishlistController {

    private final AddProductWishlist addProductWishlist;
    private final RemoveProductWishlist removeProductWishlist;
    private final GetAllProductsWishlist getAllProductsWishlist;

    @PostMapping("/items")
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

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeProduct(
            @PathVariable final String userId,
            @PathVariable final String productId) {
        removeProductWishlist.removeProduct(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @PathVariable final String userId) {

        final var products = getAllProductsWishlist.getAllProductsByUserId(userId)
                .stream()
                .map(product -> ProductResponse.builder()
                        .productId(product.getProductId())
                        .build())
        final var products = getAllProductsWishlist.getAllProductsByUserId(userId);

        return ResponseEntity.ok(products);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(final ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(WishlistNotFoundException.class)
    public ResponseEntity<String> handleWishlistNotFoundException(final WishlistNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}