package com.luizalabs.wishlist_service.adapters.in.web;

import com.luizalabs.wishlist_service.adapters.in.web.dto.request.AddProductRequest;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ProductResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.WishlistResponse;
import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/wishlists/{userId}/items")
@RequiredArgsConstructor
public class WishlistController {

    private final AddProductUseCase addProduct;
    private final RemoveProductUseCase removeProduct;
    private final ListProductsUseCase listProducts;
    private final ProductUseCase productUseCase;
    private final WishlistWebMapper mapper;

    @PostMapping
    public ResponseEntity<WishlistResponse> addProduct(
            @PathVariable final Long userId,
            @Valid @RequestBody final AddProductRequest body) {

        final var wishlist = addProduct.add(userId, body.getProductId());
        final var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(body.getProductId())
                .toUri();

        return ResponseEntity.created(location)
                .body(mapper.wishlistToResponse(wishlist));
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<Void> remove(@PathVariable final Long userId,
                                       @PathVariable final Long productId) throws Exception {
        removeProduct.remove(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<WishlistResponse> list(@PathVariable final Long userId) {
        final var wishlist = listProducts.get(userId);
        return ResponseEntity.ok(mapper.wishlistToResponse(wishlist));
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductResponse> getProductForUserWishlist(@PathVariable final Long userId,
                                                                     @PathVariable final Long productId) {
        final var wishlist = productUseCase.getProductForUserWishlist(userId, productId);
        return ResponseEntity.ok(mapper.wishlistToProductResponse(wishlist));

    }

}