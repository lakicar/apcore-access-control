package com.systemerc.apcore.dto;

import com.systemerc.apcore.entity.UserPermission;

import java.time.LocalDateTime;

public record UserPermissionResponse(
        Long userPermissionId,
        Long clientUserId,
        String permissionCode,
        String permissionName,
        Boolean allowed,
        Long grantedByClientUserId,
        LocalDateTime grantedAt
) {
    public static UserPermissionResponse from(UserPermission permission) {
        Long grantedById = permission.getGrantedByClientUser() == null
                ? null
                : permission.getGrantedByClientUser().getClientUserId();

        return new UserPermissionResponse(
                permission.getUserPermissionId(),
                permission.getClientUser().getClientUserId(),
                permission.getPermissionCode(),
                permission.getPermissionName(),
                permission.getAllowed(),
                grantedById,
                permission.getGrantedAt()
        );
    }
}
