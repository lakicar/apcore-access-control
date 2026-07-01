package com.systemerc.apcore.service;

import com.systemerc.apcore.dto.ClientRequest;
import com.systemerc.apcore.dto.CompanyRegistrationRequest;
import com.systemerc.apcore.dto.UserAccountRequest;
import com.systemerc.apcore.entity.Client;
import com.systemerc.apcore.entity.ClientUser;
import com.systemerc.apcore.entity.UserAccount;
import com.systemerc.apcore.entity.UserPermission;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
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
    private static final String DUPLICATE_EMAIL_MESSAGE =
            "Email address is already in use. Please use another email address.";
    private static final String DUPLICATE_USERNAME_MESSAGE =
            "Username is already in use. Please choose another username.";
    private static final String DUPLICATE_PHONE_MESSAGE =
            "Phone number is already in use. Please use another phone number.";

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

        verify(userAccountRepository, never()).existsByEmail(request.owner().email());
        verify(userAccountRepository, never()).existsByUsername(request.owner().username());
        verify(userAccountRepository, never()).findByEmail(request.owner().email());
        verify(userAccountRepository, never()).existsByPhone(request.owner().phone());
        verifyNoRegistrationDataSaved();
    }

    @Test
    void registerCompanyRejectsDuplicateOwnerEmailBeforeSavingRegistrationData() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432");
        when(userAccountRepository.existsByEmail(request.owner().email())).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerCompany(request))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(businessException.getMessage()).isEqualTo(DUPLICATE_EMAIL_MESSAGE);
                });

        verify(userAccountRepository).existsByEmail(request.owner().email());
        verify(userAccountRepository, never()).existsByUsername(request.owner().username());
        verify(userAccountRepository, never()).existsByPhone(request.owner().phone());
        verifyNoRegistrationDataSaved();
    }

    @Test
    void registerCompanyRejectsDuplicateOwnerUsernameBeforeSavingRegistrationData() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432");
        when(userAccountRepository.existsByUsername(request.owner().username())).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerCompany(request))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(businessException.getMessage()).isEqualTo(DUPLICATE_USERNAME_MESSAGE);
                });

        verify(userAccountRepository).existsByEmail(request.owner().email());
        verify(userAccountRepository).existsByUsername(request.owner().username());
        verify(userAccountRepository, never()).existsByPhone(request.owner().phone());
        verifyNoRegistrationDataSaved();
    }

    @Test
    void registerCompanyRejectsDuplicateOwnerPhoneBeforeSavingRegistrationDataWhenPhoneIsProvided() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432");
        when(userAccountRepository.existsByPhone(request.owner().phone())).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerCompany(request))
                .isInstanceOfSatisfying(BusinessException.class, exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(businessException.getMessage()).isEqualTo(DUPLICATE_PHONE_MESSAGE);
                });

        verify(userAccountRepository).existsByEmail(request.owner().email());
        verify(userAccountRepository).existsByUsername(request.owner().username());
        verify(userAccountRepository).existsByPhone(request.owner().phone());
        verifyNoRegistrationDataSaved();
    }

    @Test
    void registerCompanySkipsDuplicateOwnerPhoneValidationWhenPhoneIsNull() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432", null);
        stubSuccessfulRegistration(request);

        registrationService.registerCompany(request);

        verify(userAccountRepository, never()).existsByPhone(any());
    }

    @Test
    void registerCompanySkipsDuplicateOwnerPhoneValidationWhenPhoneIsBlank() {
        CompanyRegistrationRequest request = registrationRequest("123456789", "98765432", " ");
        stubSuccessfulRegistration(request);

        registrationService.registerCompany(request);

        verify(userAccountRepository, never()).existsByPhone(any());
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
        return registrationRequest(taxId, registrationNumber, "+381641111111");
    }

    private CompanyRegistrationRequest registrationRequest(String taxId, String registrationNumber, String ownerPhone) {
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
                        ownerPhone
                )
        );
    }

    private void stubSuccessfulRegistration(CompanyRegistrationRequest request) {
        when(userAccountFactory.fromRequest(request.owner())).thenReturn(userAccount(request.owner()));
        when(clientRepository.save(any(Client.class))).then(returnsFirstArg());
        when(userAccountRepository.save(any(UserAccount.class))).then(returnsFirstArg());
        when(clientUserRepository.save(any(ClientUser.class))).then(returnsFirstArg());
        when(userPermissionRepository.save(any(UserPermission.class))).then(returnsFirstArg());
    }

    private UserAccount userAccount(UserAccountRequest request) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request.username());
        userAccount.setEmail(request.email());
        userAccount.setPasswordHash("hashed-password");
        userAccount.setFirstName(request.firstName());
        userAccount.setLastName(request.lastName());
        userAccount.setDateOfBirth(request.dateOfBirth());
        userAccount.setPhone(request.phone());
        userAccount.setActive(true);
        return userAccount;
    }

    private void verifyNoRegistrationDataSaved() {
        verify(clientRepository, never()).save(any());
        verify(userAccountRepository, never()).save(any());
        verify(clientUserRepository, never()).save(any());
        verify(userPermissionRepository, never()).save(any());
    }
}
