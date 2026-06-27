package com.systemerc.apcore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank @Size(max = 20) String taxId,
        @Size(max = 20) String registrationNumber,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String address,
        @Size(max = 100) String city,
        @Size(max = 20) String postalCode,
        @Size(max = 100) String legalForm,
        @Size(max = 50) String status,
        @Email @Size(max = 255) String email,
        @Size(max = 50) String phone
) {
}
