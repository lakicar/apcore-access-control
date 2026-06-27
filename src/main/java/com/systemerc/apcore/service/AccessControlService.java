package com.systemerc.apcore.service;

import com.systemerc.apcore.entity.ClientUser;
import com.systemerc.apcore.exception.BusinessException;
import com.systemerc.apcore.repository.ClientUserRepository;
import com.systemerc.apcore.repository.UserPermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    public static final String CREATE_USER = "CREATE_USER";
    public static final String ASSIGN_PERMISSIONS = "ASSIGN_PERMISSIONS";

    private final ClientUserRepository clientUserRepository;
    private final UserPermissionRepository userPermissionRepository;

    public AccessControlService(
            ClientUserRepository clientUserRepository,
            UserPermissionRepository userPermissionRepository
    ) {
        this.clientUserRepository = clientUserRepository;
        this.userPermissionRepository = userPermissionRepository;
    }

    public ClientUser requireRequesterInClient(Long requesterClientUserId, Long clientId) {
        ClientUser requester = clientUserRepository.findByClientUserId(requesterClientUserId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Requester client_user was not found."));

        if (!requester.getClient().getClientId().equals(clientId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Requester does not belong to this client.");
        }
        if (!Boolean.TRUE.equals(requester.getActive())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Requester client_user is not active.");
        }
        return requester;
    }

    public void requireAnyPermission(Long clientUserId, String... permissionCodes) {
        for (String permissionCode : permissionCodes) {
            if (userPermissionRepository.existsByClientUserClientUserIdAndPermissionCodeAndAllowedTrue(
                    clientUserId,
                    permissionCode
            )) {
                return;
            }
        }
        throw new BusinessException(HttpStatus.FORBIDDEN, "Requester does not have required permission.");
    }
}
