package com.systemerc.apcore.controller;

import com.systemerc.apcore.dto.GrantPermissionRequest;
import com.systemerc.apcore.dto.UserPermissionResponse;
import com.systemerc.apcore.service.UserPermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/client-users")
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    public UserPermissionController(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @PostMapping("/{clientUserId}/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPermissionResponse grantPermission(
            @PathVariable Long clientUserId,
            @Valid @RequestBody GrantPermissionRequest request
    ) {
        return userPermissionService.grantPermission(clientUserId, request);
    }

    @GetMapping("/{clientUserId}/permissions")
    public List<UserPermissionResponse> listPermissions(@PathVariable Long clientUserId) {
        return userPermissionService.listPermissions(clientUserId);
    }
}
