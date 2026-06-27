package com.systemerc.apcore.dto;

import com.systemerc.apcore.entity.UserAccount;

public record UserAccountResponse(
        Long userId,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Boolean active
) {
    public static UserAccountResponse from(UserAccount userAccount) {
        return new UserAccountResponse(
                userAccount.getUserId(),
                userAccount.getUsername(),
                userAccount.getEmail(),
                userAccount.getFirstName(),
                userAccount.getLastName(),
                userAccount.getPhone(),
                userAccount.getActive()
        );
    }
}
