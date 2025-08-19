package com.luizalabs.wishlist_service.application.port.in;

import com.luizalabs.wishlist_service.domain.model.Wishlist;

public interface AddProductUseCase {
    Wishlist add(Long userId, Long productId);
}
