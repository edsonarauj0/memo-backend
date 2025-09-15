package com.nosde.memo.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank(message = "Refresh token é obrigatório") String refreshToken) {
}
