package com.systemerc.apcore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddClientUserRequest(
        @NotNull Long requesterClientUserId,
        @Valid @NotNull UserAccountRequest user,
        @NotBlank @Size(max = 50) String roleCode
) {
}
