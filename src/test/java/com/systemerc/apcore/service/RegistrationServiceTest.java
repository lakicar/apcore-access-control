package com.systemerc.apcore.service;

import com.systemerc.apcore.dto.ClientRequest;
import com.systemerc.apcore.dto.CompanyRegistrationRequest;
import com.systemerc.apcore.dto.UserAccountRequest;
import com.systemerc.apcore.exception.BusinessException;
import com.systemerc.apcore.repository.ClientRepository;
import com.systemerc.apcore.repository.ClientUserRepository;
import com.systemerc.apcore.repository.UserAccountRepository;
import com.systemerc.apcore.repository.UserPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    private static final String DUPLICATE_COMPANY_MESSAGE = "Company already exists in the system. "
            + "You cannot create another company profile with the same Tax ID or Registration Number. "
            + "If you work for this company, please contact your company administrator to create your user account.";

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ClientUserRepository clientUserRepository;

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    private UserAccountFactory userAccountFactory;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registerCompanyRejectsDuplicateCompanyByTaxIdOrRegistrationNumber() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432");
        when(clientRepository.existsByTaxIdOrRegistrationNumber("123456789", "98765432")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerCompany(request))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(businessException.getMessage()).isEqualTo(DUPLICATE_COMPANY_MESSAGE);
                });

        verify(userAccountRepository, never()).existsByUsername(request.owner().username());
        verify(userAccountRepository, never()).findByEmail(request.owner().email());
        verify(clientRepository, never()).save(any());
        verify(clientUserRepository, never()).save(any());
        verify(userPermissionRepository, never()).save(any());
    }

    @Test
    void registerCompanyChecksRegistrationNumberWhenPresent() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432");
        when(clientRepository.existsByTaxIdOrRegistrationNumber("123456789", "98765432")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerCompany(request))
                .isInstanceOf(BusinessException.class);

        verify(clientRepository).existsByTaxIdOrRegistrationNumber("123456789", "98765432");
    }

    @Test
    void duplicateCompanyLookupUsesTaxIdOnlyWhenRegistrationNumberIsBlank() {
        ClientRepository repository = mock(ClientRepository.class, CALLS_REAL_METHODS);
        when(repository.existsByTaxId("123456789")).thenReturn(false);

        assertThat(repository.existsByTaxIdOrRegistrationNumber("123456789", " ")).isFalse();

        verify(repository).existsByTaxId("123456789");
        verify(repository, never()).existsByTaxIdOrRegistrationNumberValue(any(), any());
    }

    private CompanyRegistrationRequest registrationRequest(String taxId, String registrationNumber) {
        return new CompanyRegistrationRequest(
                new ClientRequest(
                        taxId,
                        registrationNumber,
                        "Example Company",
                        "Main Street 1",
                        "Belgrade",
                        "11000",
                        "DOO",
                        "ACTIVE",
                        "office@example.com",
                        "+381111111"
                ),
                new UserAccountRequest(
                        "owner",
                        "owner@example.com",
                        "strong-password",
                        "Owner",
                        "User",
                        null,
                        "+381641111111"
                )
        );
    }
}
