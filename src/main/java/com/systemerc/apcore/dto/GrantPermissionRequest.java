package com.systemerc.apcore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GrantPermissionRequest(
        @NotNull Long requesterClientUserId,
        @NotBlank @Size(max = 100) String permissionCode,
        @Size(max = 255) String permissionName,
        Boolean allowed
) {
}
