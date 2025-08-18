package com.luizalabs.wishlist_service.exceptions;

public class WishlistMaxLimitException extends RuntimeException {
    public WishlistMaxLimitException(String message) {
        super(message);
    }
}
