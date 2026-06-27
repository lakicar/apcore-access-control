package com.systemerc.apcore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CompanyRegistrationRequest(
        @Valid @NotNull ClientRequest client,
        @Valid @NotNull UserAccountRequest owner
) {
}
