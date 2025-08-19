package com.luizalabs.wishlist_service.domain.exception;

public class WishlistMaxLimitException extends RuntimeException {
    public WishlistMaxLimitException(String message) {
        super(message);
    }
}
