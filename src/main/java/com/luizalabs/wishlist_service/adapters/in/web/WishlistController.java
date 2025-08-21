package com.luizalabs.wishlist_service.adapters.in.web;

import com.luizalabs.wishlist_service.adapters.in.web.dto.request.AddProductRequest;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ApiErrorResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ProductResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.WishlistResponse;
import com.luizalabs.wishlist_service.adapters.in.web.mapper.WishlistWebMapper;
import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("v1/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist management APIs")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final AddProductUseCase addProduct;
    private final RemoveProductUseCase removeProduct;
    private final ListProductsUseCase listProducts;
    private final ProductUseCase productUseCase;
    private final WishlistWebMapper mapper;

    @ApiResponse(responseCode = "201", description = "Product added",
            content = @Content(schema = @Schema(implementation = WishlistResponse.class)))
    @ApiResponse(responseCode = "422", description = "Wishlist limit reached",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("/{userId}/product")
    public ResponseEntity<WishlistResponse> addProduct(
            @PathVariable final Long userId,
            @Valid @RequestBody final AddProductRequest body,
            @AuthenticationPrincipal final Jwt jwt
    ) {
        final var wishlist = addProduct.add(userId, body.getProductId());
        final var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(body.getProductId())
                .toUri();

        return ResponseEntity.created(location)
                .body(mapper.wishlistToResponse(wishlist));
    }

    @Operation(summary = "Remove a product from the user's wishlist")
    @ApiResponse(responseCode = "204", description = "Removed")
    @ApiResponse(responseCode = "404", description = "Product not found in the wishlist",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping("/{userId}/product/{productId}")
    public ResponseEntity<Void> remove(@PathVariable final Long userId,
                                       @PathVariable final Long productId,
                                       @AuthenticationPrincipal final Jwt jwt
    ) throws Exception {
        removeProduct.remove(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all products from the user's wishlist")
    @ApiResponse(responseCode = "200", description = "List of products in the wishlist",
            content = @Content(schema = @Schema(implementation = WishlistResponse.class)))
    @ApiResponse(responseCode = "404", description = "Wishlist not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{userId}/products")
    public ResponseEntity<WishlistResponse> getAllProductsFromWishList(@PathVariable final Long userId,
                                                                       @AuthenticationPrincipal final Jwt jwt
    ) {
        final var wishlist = listProducts.get(userId);
        return ResponseEntity.ok(mapper.wishlistToResponse(wishlist));
    }

    @Operation(summary = "Check and get a specific product from the user's wishlist")
    @ApiResponse(responseCode = "200", description = "Product found in the wishlist",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found in the wishlist",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{userId}/product/{productId}")
    public ResponseEntity<ProductResponse> getProductForUserWishlist(@PathVariable final Long userId,
                                                                     @PathVariable final Long productId,
                                                                     @AuthenticationPrincipal final Jwt jwt
    ) {
        final var product = productUseCase.getProductForUserWishlist(userId, productId);
        return product.map(aLong -> ResponseEntity.ok(mapper.productIdToProductResponse(aLong)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
