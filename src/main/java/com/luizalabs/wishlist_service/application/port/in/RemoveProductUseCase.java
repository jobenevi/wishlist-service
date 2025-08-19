package com.luizalabs.wishlist_service.application.port.in;

public interface RemoveProductUseCase {

    void remove(Long userId, Long productId) throws Exception;

}
