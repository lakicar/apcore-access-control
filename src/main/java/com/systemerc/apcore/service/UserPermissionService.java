package com.systemerc.apcore.service;

import com.systemerc.apcore.dto.GrantPermissionRequest;
import com.systemerc.apcore.dto.UserPermissionResponse;
import com.systemerc.apcore.entity.ClientUser;
import com.systemerc.apcore.entity.UserPermission;
import com.systemerc.apcore.exception.BusinessException;
import com.systemerc.apcore.repository.ClientUserRepository;
import com.systemerc.apcore.repository.UserPermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPermissionService {

    private final ClientUserRepository clientUserRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final AccessControlService accessControlService;

    public UserPermissionService(
            ClientUserRepository clientUserRepository,
            UserPermissionRepository userPermissionRepository,
            AccessControlService accessControlService
    ) {
        this.clientUserRepository = clientUserRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.accessControlService = accessControlService;
    }

    @Transactional
    public UserPermissionResponse grantPermission(Long clientUserId, GrantPermissionRequest request) {
        ClientUser target = clientUserRepository.findByClientUserId(clientUserId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Target client_user was not found."));

        ClientUser requester = accessControlService.requireRequesterInClient(
                request.requesterClientUserId(),
                target.getClient().getClientId()
        );
        accessControlService.requireAnyPermission(requester.getClientUserId(), AccessControlService.ASSIGN_PERMISSIONS);

        UserPermission permission = userPermissionRepository
                .findByClientUserClientUserIdAndPermissionCode(clientUserId, request.permissionCode())
                .orElseGet(UserPermission::new);

        permission.setClientUser(target);
        permission.setPermissionCode(request.permissionCode());
        permission.setPermissionName(request.permissionName());
        permission.setAllowed(request.allowed() == null || request.allowed());
        permission.setGrantedByClientUser(requester);
        permission.setGrantedAt(LocalDateTime.now());

        return UserPermissionResponse.from(userPermissionRepository.save(permission));
    }

    @Transactional(readOnly = true)
    public List<UserPermissionResponse> listPermissions(Long clientUserId) {
        if (!clientUserRepository.existsById(clientUserId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Client_user was not found.");
        }

        return userPermissionRepository.findByClientUserClientUserIdOrderByGrantedAtAsc(clientUserId).stream()
                .map(UserPermissionResponse::from)
                .toList();
    }
}
