package com.luizalabs.wishlist_service.config;

import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.application.service.WishlistService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BeanConfig {

    @Bean
    @Primary
    public WishlistService wishlistService(WishlistRepositoryPort repository) {
        return new WishlistService(repository);
    }

    @Bean
    public AddProductUseCase addProductUseCase(WishlistService service) {
        return service;
    }

    @Bean
    public RemoveProductUseCase removeProductUseCase(WishlistService service) {
        return service;
    }

    @Bean
    public ListProductsUseCase listProductsUseCase(WishlistService service)  {
        return service;
    }

    @Bean
    public ProductUseCase productUseCase(WishlistService service) {
        return service;
    }

}