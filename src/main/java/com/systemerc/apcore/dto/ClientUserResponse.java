package com.systemerc.apcore.dto;

import com.systemerc.apcore.entity.ClientUser;

public record ClientUserResponse(
        Long clientUserId,
        Long clientId,
        UserAccountResponse user,
        String roleCode,
        Boolean active
) {
    public static ClientUserResponse from(ClientUser clientUser) {
        return new ClientUserResponse(
                clientUser.getClientUserId(),
                clientUser.getClient().getClientId(),
                UserAccountResponse.from(clientUser.getUserAccount()),
                clientUser.getRoleCode(),
                clientUser.getActive()
        );
    }
}
