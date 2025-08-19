package com.luizalabs.wishlist_service.application.port.in;

import com.luizalabs.wishlist_service.domain.model.Wishlist;

public interface RemoveProductUseCase {

    Wishlist remove(Long userId, Long productId) throws Exception;

}
