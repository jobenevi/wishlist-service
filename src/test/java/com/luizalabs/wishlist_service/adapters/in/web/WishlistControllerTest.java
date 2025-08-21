package com.luizalabs.wishlist_service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalabs.wishlist_service.adapters.in.web.dto.request.AddProductRequest;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.ProductResponse;
import com.luizalabs.wishlist_service.adapters.in.web.dto.response.WishlistResponse;
import com.luizalabs.wishlist_service.adapters.in.web.mapper.WishlistWebMapper;
import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddProductUseCase addProductUseCase;
    @MockitoBean
    private RemoveProductUseCase removeProductUseCase;
    @MockitoBean
    private ListProductsUseCase listProductsUseCase;
    @MockitoBean
    private ProductUseCase productUseCase;
    @MockitoBean
    private WishlistWebMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(addProductUseCase, removeProductUseCase, listProductsUseCase, productUseCase, mapper);
    }

    @Test
    @DisplayName("addProduct returns 201 when userId matches JWT and product is added")
    void addProduct_ReturnsCreated_WhenUserIdMatchesJwt() throws Exception {
        Long userId = 1L;
        Long productId = 42L;
        AddProductRequest request = new AddProductRequest(productId);
        Wishlist wishlist = Wishlist.rehydrate(userId, List.of(productId));
        WishlistResponse response = WishlistResponse.builder().userId(userId).productIds(List.of(productId)).build();

        Mockito.when(addProductUseCase.add(eq(userId), eq(productId))).thenReturn(wishlist);
        Mockito.when(mapper.wishlistToResponse(wishlist)).thenReturn(response);

        mockMvc.perform(post("/v1/wishlists/{userId}/product", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productIds[0]").value(productId));
    }

    @Test
    @DisplayName("addProduct returns 403 when userId does not match JWT")
    void addProduct_ReturnsForbidden_WhenUserIdDoesNotMatchJwt() throws Exception {
        Long pathUserId = 1L;
        Long jwtUserId = 2L;
        Long productId = 42L;
        AddProductRequest request = new AddProductRequest(productId);

        mockMvc.perform(post("/v1/wishlists/{userId}/product", pathUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(jwtUserId)))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("addProduct returns 400 when productId is null")
    void addProduct_ReturnsBadRequest_WhenProductIdIsNull() throws Exception {
        Long userId = 1L;
        AddProductRequest request = new AddProductRequest(null);

        mockMvc.perform(post("/v1/wishlists/{userId}/product", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("removeProduct returns 204 when userId matches JWT and product is removed")
    void removeProduct_ReturnsNoContent_WhenUserIdMatchesJwt() throws Exception {
        Long userId = 1L;
        Long productId = 42L;
        Mockito.doNothing().when(removeProductUseCase).remove(userId, productId);

        mockMvc.perform(delete("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("removeProduct returns 403 when userId does not match JWT")
    void removeProduct_ReturnsForbidden_WhenUserIdDoesNotMatchJwt() throws Exception {
        Long pathUserId = 1L;
        Long jwtUserId = 2L;
        Long productId = 42L;
        mockMvc.perform(delete("/v1/wishlists/{userId}/product/{productId}", pathUserId, productId)
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(jwtUserId)))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("removeProduct returns 404 when product is not found in wishlist")
    void removeProduct_ReturnsNotFound_WhenProductNotFound() throws Exception {
        Long userId = 1L;
        Long productId = 42L;
        Mockito.doThrow(new com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException("Product not found"))
                .when(removeProductUseCase).remove(userId, productId);

        mockMvc.perform(delete("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAllProductsFromWishList returns 200 when userId matches JWT and wishlist exists")
    void getAllProductsFromWishList_ReturnsOk_WhenUserIdMatchesJwtAndWishlistExists() throws Exception {
        Long userId = 1L;
        List<Long> productIds = List.of(10L, 20L);
        Wishlist wishlist = Wishlist.rehydrate(userId, productIds);
        WishlistResponse response = WishlistResponse.builder().userId(userId).productIds(productIds).build();
        Mockito.when(listProductsUseCase.get(userId)).thenReturn(wishlist);
        Mockito.when(mapper.wishlistToResponse(wishlist)).thenReturn(response);

        mockMvc.perform(get("/v1/wishlists/{userId}/products", userId)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productIds[0]").value(10L))
                .andExpect(jsonPath("$.productIds[1]").value(20L));
    }

    @Test
    @DisplayName("getAllProductsFromWishList returns 403 when userId does not match JWT")
    void getAllProductsFromWishList_ReturnsForbidden_WhenUserIdDoesNotMatchJwt() throws Exception {
        Long pathUserId = 1L;
        Long jwtUserId = 2L;
        mockMvc.perform(get("/v1/wishlists/{userId}/products", pathUserId)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(jwtUserId)))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("getAllProductsFromWishList returns 404 when wishlist not found")
    void getAllProductsFromWishList_ReturnsNotFound_WhenWishlistNotFound() throws Exception {
        Long userId = 1L;
        Mockito.when(listProductsUseCase.get(userId)).thenThrow(new com.luizalabs.wishlist_service.domain.exception.WishlistNotFoundException("Wishlist not found"));

        mockMvc.perform(get("/v1/wishlists/{userId}/products", userId)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getProductForUserWishlist returns 200 when product exists and userId matches JWT")
    void getProductForUserWishlist_ReturnsOk_WhenProductExistsAndUserIdMatchesJwt() throws Exception {
        Long userId = 1L;
        Long productId = 42L;
        Mockito.when(productUseCase.getProductForUserWishlist(userId, productId)).thenReturn(java.util.Optional.of(productId));
        ProductResponse response = ProductResponse.builder().productId(productId).build();
        Mockito.when(mapper.productIdToProductResponse(productId)).thenReturn(response);

        mockMvc.perform(get("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    @DisplayName("getProductForUserWishlist returns 404 when product does not exist in wishlist")
    void getProductForUserWishlist_ReturnsNotFound_WhenProductDoesNotExist() throws Exception {
        Long userId = 1L;
        Long productId = 42L;
        Mockito.when(productUseCase.getProductForUserWishlist(userId, productId)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(userId)))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getProductForUserWishlist returns 403 when userId does not match JWT")
    void getProductForUserWishlist_ReturnsForbidden_WhenUserIdDoesNotMatchJwt() throws Exception {
        Long pathUserId = 1L;
        Long jwtUserId = 2L;
        Long productId = 42L;
        mockMvc.perform(get("/v1/wishlists/{userId}/product/{productId}", pathUserId, productId)
                .with(jwt().jwt(jwt -> jwt.subject(String.valueOf(jwtUserId)))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("validateUserId allows access when JWT has sub claim matching path userId and no user_id claim")
    void validateUserId_AllowsAccess_WhenJwtHasSubClaimMatchingPath() {
        Long pathUserId = 456L;
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim("user_id")).thenReturn(false);
        Mockito.when(jwt.hasClaim("sub")).thenReturn(true);
        Mockito.when(jwt.getSubject()).thenReturn(String.valueOf(pathUserId));
        // Should not throw
        try {
            var method = WishlistController.class.getDeclaredMethod("validateUserId", Long.class, Jwt.class);
            method.setAccessible(true);
            method.invoke(new WishlistController(null, null, null, null, null), pathUserId, jwt);
        } catch (Exception e) {
            throw new AssertionError("Should not throw", e);
        }
    }

    @Test
    @DisplayName("validateUserId denies access when JWT sub claim does not match path userId and no user_id claim")
    void validateUserId_DeniesAccess_WhenJwtSubClaimDoesNotMatchPath() {
        Long pathUserId = 123L;
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim("user_id")).thenReturn(false);
        Mockito.when(jwt.hasClaim("sub")).thenReturn(true);
        Mockito.when(jwt.getSubject()).thenReturn("999");
        try {
            var method = WishlistController.class.getDeclaredMethod("validateUserId", Long.class, Jwt.class);
            method.setAccessible(true);
            method.invoke(new WishlistController(null, null, null, null, null), pathUserId, jwt);
            throw new AssertionError("Should have thrown ResponseStatusException");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof ResponseStatusException rse)) {
                throw new AssertionError("Expected ResponseStatusException", e);
            }
            assertEquals(org.springframework.http.HttpStatus.FORBIDDEN, rse.getStatusCode());
        }
    }

    @Test
    @DisplayName("validateUserId denies access when JWT has neither user_id nor sub claim")
    void validateUserId_DeniesAccess_WhenJwtHasNeitherUserIdNorSubClaim() {
        Long pathUserId = 123L;
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim("user_id")).thenReturn(false);
        Mockito.when(jwt.hasClaim("sub")).thenReturn(false);
        try {
            var method = WishlistController.class.getDeclaredMethod("validateUserId", Long.class, Jwt.class);
            method.setAccessible(true);
            method.invoke(new WishlistController(null, null, null, null, null), pathUserId, jwt);
            throw new AssertionError("Should have thrown ResponseStatusException");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof ResponseStatusException rse)) {
                throw new AssertionError("Expected ResponseStatusException", e);
            }
            assertEquals(org.springframework.http.HttpStatus.FORBIDDEN, rse.getStatusCode());
        }
    }

}
