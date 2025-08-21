package com.luizalabs.wishlist_service.config;

import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.application.service.WishlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BeanConfigTest {

    @Test
    @DisplayName("wishlistService bean should return WishlistService with injected repository")
    void wishlistServiceBeanReturnsWishlistService() {
        WishlistRepositoryPort repo = mock(WishlistRepositoryPort.class);
        BeanConfig config = new BeanConfig();
        WishlistService service = config.wishlistService(repo);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("addProductUseCase bean should return same WishlistService instance")
    void addProductUseCaseBeanReturnsSameInstance() {
        WishlistService service = mock(WishlistService.class);
        BeanConfig config = new BeanConfig();
        AddProductUseCase useCase = config.addProductUseCase(service);
        assertThat(useCase).isSameAs(service);
    }

    @Test
    @DisplayName("removeProductUseCase bean should return same WishlistService instance")
    void removeProductUseCaseBeanReturnsSameInstance() {
        WishlistService service = mock(WishlistService.class);
        BeanConfig config = new BeanConfig();
        RemoveProductUseCase useCase = config.removeProductUseCase(service);
        assertThat(useCase).isSameAs(service);
    }

    @Test
    @DisplayName("listProductsUseCase bean should return same WishlistService instance")
    void listProductsUseCaseBeanReturnsSameInstance() {
        WishlistService service = mock(WishlistService.class);
        BeanConfig config = new BeanConfig();
        ListProductsUseCase useCase = config.listProductsUseCase(service);
        assertThat(useCase).isSameAs(service);
    }

    @Test
    @DisplayName("productUseCase bean should return same WishlistService instance")
    void productUseCaseBeanReturnsSameInstance() {
        WishlistService service = mock(WishlistService.class);
        BeanConfig config = new BeanConfig();
        ProductUseCase useCase = config.productUseCase(service);
        assertThat(useCase).isSameAs(service);
    }
}
