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
import com.luizalabs.wishlist_service.domain.exception.ProductNotFoundException;
import com.luizalabs.wishlist_service.domain.model.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AddProductUseCase addProductUseCase;
    @MockitoBean private RemoveProductUseCase removeProductUseCase;
    @MockitoBean private ListProductsUseCase listProductsUseCase;
    @MockitoBean private ProductUseCase productUseCase;
    @MockitoBean private WishlistWebMapper mapper;

    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(addProductUseCase, removeProductUseCase, listProductsUseCase, productUseCase, mapper);
    }

    @Test
    @DisplayName("Should add product to wishlist and return created response")
    void addProductToWishlist_ReturnsCreated() throws Exception {
        Long userId = 1L;
        Long productId = 3L;
        var request = new AddProductRequest(productId);
        var wishlist = Wishlist.rehydrate(userId, List.of(productId));
        var response = WishlistResponse.builder().userId(userId).productIds(List.of(productId)).build();

        Mockito.when(addProductUseCase.add(eq(userId), eq(productId))).thenReturn(wishlist);
        Mockito.when(mapper.wishlistToResponse(wishlist)).thenReturn(response);

        mockMvc.perform(post("/v1/wishlists/{userId}/product", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer test")   // controller requires the header
                        .with(jwt())                               // inject authenticated principal
                        .with(csrf()))                             // avoid 403
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productIds[0]").value(productId));
    }

    @Test
    @DisplayName("Should remove product from wishlist and return no content")
    void removeProductFromWishlist_ReturnsNoContent() throws Exception {
        Long userId = 1L;
        Long productId = 3L;
        Mockito.doNothing().when(removeProductUseCase).remove(userId, productId);

        mockMvc.perform(delete("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                        .header("Authorization", "Bearer test")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should get all products from wishlist")
    void getAllProductsFromWishlist_ReturnsOk() throws Exception {
        Long userId = 1L;
        var productIds = List.of(3L, 4L);
        var wishlist = Wishlist.rehydrate(userId, productIds);
        var response = WishlistResponse.builder().userId(userId).productIds(productIds).build();

        Mockito.when(listProductsUseCase.get(userId)).thenReturn(wishlist);
        Mockito.when(mapper.wishlistToResponse(wishlist)).thenReturn(response);

        mockMvc.perform(get("/v1/wishlists/{userId}/products", userId)
                        .header("Authorization", "Bearer test")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productIds[0]").value(3L))
                .andExpect(jsonPath("$.productIds[1]").value(4L));
    }

    @Test
    @DisplayName("Should get a specific product from wishlist")
    void getProductForUserWishlist_ReturnsOk() throws Exception {
        Long userId = 1L;
        Long productId = 3L;
        var wishlist = Wishlist.rehydrate(userId, List.of(productId));
        var response = ProductResponse.builder().productId(productId).build();

        Mockito.when(productUseCase.getProductForUserWishlist(userId, productId)).thenReturn(wishlist);
        Mockito.when(mapper.wishlistToProductResponse(wishlist)).thenReturn(response);

        mockMvc.perform(get("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                        .header("Authorization", "Bearer test")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    @DisplayName("Should return 400 when adding product with invalid body")
    void addProductToWishlist_InvalidBody_ReturnsBadRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/v1/wishlists/{userId}/product", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("Authorization", "Bearer test")
                        .with(jwt())
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when product not found in wishlist")
    void getProductForUserWishlist_ProductNotFound_ReturnsNotFound() throws Exception {
        Long userId = 1L;
        Long productId = 99L;

        Mockito.when(productUseCase.getProductForUserWishlist(userId, productId))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/v1/wishlists/{userId}/product/{productId}", userId, productId)
                        .header("Authorization", "Bearer test")
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }
}
