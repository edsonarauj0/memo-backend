package com.nosde.memo.application.dto;

public record AuthResponse(String accessToken, String refreshToken, UserDto user) {
}
