package com.luizalabs.wishlist_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.ProductResponse;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.exceptions.ProductNotFoundException;
import com.luizalabs.wishlist_service.exceptions.WishlistNotFoundException;
import com.luizalabs.wishlist_service.usecase.AddProductWishlist;
import com.luizalabs.wishlist_service.usecase.GetAllProductsWishlist;
import com.luizalabs.wishlist_service.usecase.RemoveProductWishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddProductWishlist addProductWishlist;

    @MockitoBean
    private RemoveProductWishlist removeProductWishlist;

    @MockitoBean
    private GetAllProductsWishlist getAllProductsWishlist;

    @Autowired
    private ObjectMapper objectMapper;

    private String userId;
    private WishlistProductRequest request;
    private WishlistProductResponse response;

    @BeforeEach
    void setUp() {
        userId = "user-1";
        request = WishlistProductRequest.builder().productId("prod-1").build();
        response = WishlistProductResponse.builder()
                .userId(userId)
                .productIds(Set.of("prod-1"))
                .build();
    }

    @Test
    void addProductReturnsCreatedStatusAndLocationHeader() throws Exception {
        Mockito.when(addProductWishlist.addProduct(eq(userId), any(WishlistProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/wishlists/{userId}/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/wishlists/user-1/items/prod-1"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productIds[0]").value("prod-1"));
    }

    @Test
    void notAddProductReturnsBadRequestWhenProductIdIsMissing() throws Exception {
        WishlistProductRequest invalidRequest = WishlistProductRequest.builder().build();

        mockMvc.perform(post("/wishlists/{userId}/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeProductReturnsNoContentWhenProductExists() throws Exception {
        final String productId = "prod-1";
        Mockito.doNothing().when(removeProductWishlist).removeProduct(userId, productId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/wishlists/{userId}/items/{productId}", userId, productId))
                .andExpect(status().isNoContent());

        Mockito.verify(removeProductWishlist).removeProduct(userId, productId);
    }

    @Test
    void removeProductReturnsNoContentWhenProductNotExists() throws Exception {
        final String productId = "prod-1";
        Mockito.doThrow(new ProductNotFoundException("Product not found: " + productId))
                .when(removeProductWishlist).removeProduct(userId, productId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/wishlists/{userId}/items/{productId}", userId, productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllProductsReturnsOkAndProductListWhenWishlistExists() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().productId("prod-1").build(),
                ProductResponse.builder().productId("prod-2").build()
        );
        Mockito.when(getAllProductsWishlist.getAllProductsByUserId(userId)).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/wishlists/{userId}/items", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productId").value("prod-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].productId").value("prod-2"));
    }

    @Test
    void getAllProductsReturnsEmptyListWhenWishlistHasNoProducts() throws Exception {
        Mockito.when(getAllProductsWishlist.getAllProductsByUserId(userId)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/wishlists/{userId}/items", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void getAllProductsReturnsNotFoundWhenWishlistDoesNotExist() throws Exception {
        Mockito.when(getAllProductsWishlist.getAllProductsByUserId(userId))
                .thenThrow(new WishlistNotFoundException("Wishlist not found for user: " + userId));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/wishlists/{userId}/items", userId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}