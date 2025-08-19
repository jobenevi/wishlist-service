package com.luizalabs.wishlist_service.config;

import com.luizalabs.wishlist_service.application.port.in.AddProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ProductUseCase;
import com.luizalabs.wishlist_service.application.port.in.ListProductsUseCase;
import com.luizalabs.wishlist_service.application.port.in.RemoveProductUseCase;
import com.luizalabs.wishlist_service.application.port.out.WishlistRepositoryPort;
import com.luizalabs.wishlist_service.application.service.WishlistService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public WishlistService wishlistService(WishlistRepositoryPort repository) {
        return new WishlistService(repository);
    }

    @Bean
    public AddProductUseCase addProductUseCase(WishlistService service) {
        return service;
    }

    @Bean
    public RemoveProductUseCase removeProductUseCase(WishlistService s) {
        return s;
    }

    @Bean
    public ListProductsUseCase listProductsUseCase(WishlistService s)  {
        return s;
    }

    @Bean
    public ProductUseCase checkProductUseCase(WishlistService s) {
        return s;
    }

}
