package com.luizalabs.wishlist_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalabs.wishlist_service.controller.request.WishlistProductRequest;
import com.luizalabs.wishlist_service.controller.response.WishlistProductResponse;
import com.luizalabs.wishlist_service.usecase.AddProductWishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}