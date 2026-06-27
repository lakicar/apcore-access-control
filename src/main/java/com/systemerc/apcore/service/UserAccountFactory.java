package com.systemerc.apcore.service;

import com.systemerc.apcore.dto.UserAccountRequest;
import com.systemerc.apcore.entity.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountFactory {

    private final PasswordHashService passwordHashService;

    public UserAccountFactory(PasswordHashService passwordHashService) {
        this.passwordHashService = passwordHashService;
    }

    public UserAccount fromRequest(UserAccountRequest request) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request.username());
        userAccount.setEmail(request.email());
        userAccount.setPasswordHash(passwordHashService.hash(request.password()));
        userAccount.setFirstName(request.firstName());
        userAccount.setLastName(request.lastName());
        userAccount.setDateOfBirth(request.dateOfBirth());
        userAccount.setPhone(request.phone());
        userAccount.setActive(true);
        return userAccount;
    }
}
