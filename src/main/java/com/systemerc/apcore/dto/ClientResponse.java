package com.systemerc.apcore.dto;

import com.systemerc.apcore.entity.Client;

public record ClientResponse(
        Long clientId,
        String taxId,
        String registrationNumber,
        String name,
        String address,
        String city,
        String postalCode,
        String legalForm,
        String status,
        String email,
        String phone,
        Boolean active
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getClientId(),
                client.getTaxId(),
                client.getRegistrationNumber(),
                client.getName(),
                client.getAddress(),
                client.getCity(),
                client.getPostalCode(),
                client.getLegalForm(),
                client.getStatus(),
                client.getEmail(),
                client.getPhone(),
                client.getActive()
        );
    }
}
