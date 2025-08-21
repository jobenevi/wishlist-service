package com.luizalabs.wishlist_service.application.port.in;

import java.util.Optional;

public interface ProductUseCase {

    Optional<Long> getProductForUserWishlist(Long userId, Long productId);

}
