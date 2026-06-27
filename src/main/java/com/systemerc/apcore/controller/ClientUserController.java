package com.systemerc.apcore.controller;

import com.systemerc.apcore.dto.AddClientUserRequest;
import com.systemerc.apcore.dto.ClientUserResponse;
import com.systemerc.apcore.service.ClientUserService;
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
@RequestMapping("/api/clients")
public class ClientUserController {

    private final ClientUserService clientUserService;

    public ClientUserController(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    @PostMapping("/{clientId}/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientUserResponse addUserToClient(
            @PathVariable Long clientId,
            @Valid @RequestBody AddClientUserRequest request
    ) {
        return clientUserService.addUserToClient(clientId, request);
    }

    @GetMapping("/{clientId}/users")
    public List<ClientUserResponse> listClientUsers(@PathVariable Long clientId) {
        return clientUserService.listClientUsers(clientId);
    }
}
