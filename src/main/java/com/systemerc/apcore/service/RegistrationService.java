package com.systemerc.apcore.service;

import com.systemerc.apcore.dto.ClientRequest;
import com.systemerc.apcore.dto.ClientResponse;
import com.systemerc.apcore.dto.ClientUserResponse;
import com.systemerc.apcore.dto.CompanyRegistrationRequest;
import com.systemerc.apcore.dto.CompanyRegistrationResponse;
import com.systemerc.apcore.dto.UserPermissionResponse;
import com.systemerc.apcore.entity.Client;
import com.systemerc.apcore.entity.ClientUser;
import com.systemerc.apcore.entity.UserAccount;
import com.systemerc.apcore.entity.UserPermission;
import com.systemerc.apcore.exception.BusinessException;
import com.systemerc.apcore.repository.ClientRepository;
import com.systemerc.apcore.repository.ClientUserRepository;
import com.systemerc.apcore.repository.UserAccountRepository;
import com.systemerc.apcore.repository.UserPermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {

    private static final List<String> OWNER_PERMISSIONS = List.of(
            "CREATE_USER",
            "UPDATE_USER",
            "DEACTIVATE_USER",
            "ASSIGN_PERMISSIONS",
            "VIEW_CLIENT",
            "UPDATE_CLIENT",
            "ADMIN_PANEL"
    );

    private final ClientRepository clientRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClientUserRepository clientUserRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserAccountFactory userAccountFactory;

    public RegistrationService(
            ClientRepository clientRepository,
            UserAccountRepository userAccountRepository,
            ClientUserRepository clientUserRepository,
            UserPermissionRepository userPermissionRepository,
            UserAccountFactory userAccountFactory
    ) {
        this.clientRepository = clientRepository;
        this.userAccountRepository = userAccountRepository;
        this.clientUserRepository = clientUserRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.userAccountFactory = userAccountFactory;
    }

    @Transactional
    public CompanyRegistrationResponse registerCompany(CompanyRegistrationRequest request) {
        if (clientRepository.existsByTaxId(request.client().taxId())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Firma sa unetim tax_id vec postoji.");
        }
        if (userAccountRepository.existsByUsername(request.owner().username())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Username vec postoji.");
        }
        if (userAccountRepository.findByEmail(request.owner().email()).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email vec postoji.");
        }

        Client client = clientRepository.save(toClient(request.client()));
        UserAccount ownerAccount = userAccountRepository.save(userAccountFactory.fromRequest(request.owner()));

        ClientUser owner = new ClientUser();
        owner.setClient(client);
        owner.setUserAccount(ownerAccount);
        owner.setRoleCode("OWNER");
        owner.setActive(true);
        ClientUser savedOwner = clientUserRepository.save(owner);

        List<UserPermission> permissions = OWNER_PERMISSIONS.stream()
                .map(permissionCode -> createPermission(savedOwner, permissionCode, permissionName(permissionCode), savedOwner))
                .map(userPermissionRepository::save)
                .toList();

        return new CompanyRegistrationResponse(
                ClientResponse.from(client),
                ClientUserResponse.from(savedOwner),
                permissions.stream().map(UserPermissionResponse::from).toList()
        );
    }

    private Client toClient(ClientRequest request) {
        Client client = new Client();
        client.setTaxId(request.taxId());
        client.setRegistrationNumber(request.registrationNumber());
        client.setName(request.name());
        client.setAddress(request.address());
        client.setCity(request.city());
        client.setPostalCode(request.postalCode());
        client.setLegalForm(request.legalForm());
        client.setStatus(request.status());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setDataSource("REGISTRATION");
        client.setActive(true);
        return client;
    }

    private UserPermission createPermission(
            ClientUser clientUser,
            String permissionCode,
            String permissionName,
            ClientUser grantedBy
    ) {
        UserPermission permission = new UserPermission();
        permission.setClientUser(clientUser);
        permission.setPermissionCode(permissionCode);
        permission.setPermissionName(permissionName);
        permission.setAllowed(true);
        permission.setGrantedByClientUser(grantedBy);
        return permission;
    }

    private String permissionName(String permissionCode) {
        return permissionCode.replace('_', ' ');
    }
}
