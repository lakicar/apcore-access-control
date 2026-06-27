package com.systemerc.apcore.dto;

import java.util.List;

public record CompanyRegistrationResponse(
        ClientResponse client,
        ClientUserResponse owner,
        List<UserPermissionResponse> permissions
) {
}
