package com.luizalabs.wishlist_service.application.port.in;

import com.luizalabs.wishlist_service.domain.model.Wishlist;

public interface ProductUseCase {

    Wishlist getProductForUserWishlist(Long userId, Long productId);

}
