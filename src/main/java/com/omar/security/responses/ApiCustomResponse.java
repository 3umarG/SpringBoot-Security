package com.omar.security.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
public record ApiCustomResponse<T>(String message, int statusCode, Boolean isSuccess, T data) {
}
