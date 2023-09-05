package com.omar.security.responses;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public final class ApiCustomResponse<T> {
    private final String message;
    private final int statusCode;
    private final Boolean isSuccess;
    private final T data;

}
