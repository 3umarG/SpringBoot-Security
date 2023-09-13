package com.omar.security.entities.projection;

/**
 * Projection for {@link com.omar.security.entities.RefreshToken}
 */
public interface RefreshTokenProjection {
    String getToken();
    Integer getTokenUserId();
}