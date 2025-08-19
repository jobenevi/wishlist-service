package com.luizalabs.wishlist_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class WishlistServiceApplicationTest {
    @Test
    @DisplayName("main should run Spring Boot application without exception")
    void mainRunsSpringBootApplication() {
        assertThatCode(() -> WishlistServiceApplication.main(new String[]{}))
                .doesNotThrowAnyException();
    }
}