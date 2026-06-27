package com.systemerc.apcore.controller;

import com.systemerc.apcore.dto.CompanyRegistrationRequest;
import com.systemerc.apcore.dto.CompanyRegistrationResponse;
import com.systemerc.apcore.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyRegistrationResponse registerCompany(@Valid @RequestBody CompanyRegistrationRequest request) {
        return registrationService.registerCompany(request);
    }
}
