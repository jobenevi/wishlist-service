package com.luizalabs.wishlist_service.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    @DisplayName("OpenApiConfig is annotated with @Configuration and @SecurityScheme")
    void openApiConfig_hasRequiredAnnotations() {
        Configuration configuration = OpenApiConfig.class.getAnnotation(Configuration.class);
        SecurityScheme securityScheme = OpenApiConfig.class.getAnnotation(SecurityScheme.class);

        assertThat(configuration).isNotNull();
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.name()).isEqualTo("bearerAuth");
        assertThat(securityScheme.type()).isEqualTo(SecuritySchemeType.HTTP);
        assertThat(securityScheme.scheme()).isEqualTo("bearer");
        assertThat(securityScheme.bearerFormat()).isEqualTo("JWT");
    }

    @Test
    @DisplayName("OpenApiConfig can be instantiated with default constructor")
    void openApiConfig_canBeInstantiated() {
        OpenApiConfig config = new OpenApiConfig();
        assertThat(config).isNotNull();
    }
}

