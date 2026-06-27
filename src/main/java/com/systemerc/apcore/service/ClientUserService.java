package com.systemerc.apcore.service;

import com.systemerc.apcore.dto.AddClientUserRequest;
import com.systemerc.apcore.dto.ClientUserResponse;
import com.systemerc.apcore.entity.Client;
import com.systemerc.apcore.entity.ClientUser;
import com.systemerc.apcore.entity.UserAccount;
import com.systemerc.apcore.exception.BusinessException;
import com.systemerc.apcore.repository.ClientRepository;
import com.systemerc.apcore.repository.ClientUserRepository;
import com.systemerc.apcore.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientUserService {

    private final ClientRepository clientRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClientUserRepository clientUserRepository;
    private final UserAccountFactory userAccountFactory;
    private final AccessControlService accessControlService;

    public ClientUserService(
            ClientRepository clientRepository,
            UserAccountRepository userAccountRepository,
            ClientUserRepository clientUserRepository,
            UserAccountFactory userAccountFactory,
            AccessControlService accessControlService
    ) {
        this.clientRepository = clientRepository;
        this.userAccountRepository = userAccountRepository;
        this.clientUserRepository = clientUserRepository;
        this.userAccountFactory = userAccountFactory;
        this.accessControlService = accessControlService;
    }

    @Transactional
    public ClientUserResponse addUserToClient(Long clientId, AddClientUserRequest request) {
        ClientUser requester = accessControlService.requireRequesterInClient(request.requesterClientUserId(), clientId);
        accessControlService.requireAnyPermission(
                requester.getClientUserId(),
                AccessControlService.CREATE_USER,
                AccessControlService.ASSIGN_PERMISSIONS
        );

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Client was not found."));

        UserAccount userAccount = userAccountRepository.findByEmail(request.user().email())
                .orElseGet(() -> createUserAccount(request));

        if (clientUserRepository.existsByClientClientIdAndUserAccountUserId(clientId, userAccount.getUserId())) {
            throw new BusinessException(HttpStatus.CONFLICT, "User is already assigned to this client.");
        }

        ClientUser clientUser = new ClientUser();
        clientUser.setClient(client);
        clientUser.setUserAccount(userAccount);
        clientUser.setRoleCode(request.roleCode());
        clientUser.setActive(true);

        return ClientUserResponse.from(clientUserRepository.save(clientUser));
    }

    @Transactional(readOnly = true)
    public List<ClientUserResponse> listClientUsers(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Client was not found.");
        }

        return clientUserRepository.findByClientClientIdOrderByCreatedAtAsc(clientId).stream()
                .map(ClientUserResponse::from)
                .toList();
    }

    private UserAccount createUserAccount(AddClientUserRequest request) {
        if (userAccountRepository.existsByUsername(request.user().username())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Username vec postoji.");
        }
        return userAccountRepository.save(userAccountFactory.fromRequest(request.user()));
    }
}
