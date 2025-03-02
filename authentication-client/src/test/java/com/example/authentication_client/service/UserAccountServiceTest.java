package com.example.authentication_client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.authentication_client.DTO.PasswordDTO;
import com.example.authentication_client.DTO.UserAccountDTO;
import com.example.authentication_client.DTO.UserInfo;
import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.repository.UserAccountRepository;
import com.example.authentication_client.kafka.KafkaProducerService;
import com.example.authentication_client.utils.AuntResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {
    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAccountService = new UserAccountService
                (userAccountRepository,
                        passwordEncoder,
                        jwtService,
                        authenticationManager,
                        kafkaProducerService);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/test_users.csv")
    void testRegistrationExpectedTrue(String email, String username, String password, String doublePassword) {
        UserAccountDTO request = new UserAccountDTO(email, username, password, doublePassword);

        assertNotNull(passwordEncoder, "PasswordEncoder is null in test!");

        when(userAccountRepository.existsUserAccountByEmailOrUsername(email, username)).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("dummyEncodedPassword");

        UserAccount savedUser = new UserAccount();
        savedUser.setId(1L);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword("dummyEncodedPassword");

        when(userAccountRepository.save(any())).thenReturn(savedUser);

        boolean result = userAccountService.registration(request);

        assertTrue(result);

        verify(userAccountRepository, times(1)).save(any());
        verify(kafkaProducerService, times(1)).sendCreatNewUserAccountEvent(anyLong());
    }
    

    @ParameterizedTest
    @CsvFileSource(resources = "/test_users.csv")
    void testValidUserRegistration(String email, String username, String password, String doublePassword) {
        UserAccountDTO request = new UserAccountDTO(email, username, password, doublePassword);
        Set<ConstraintViolation<UserAccountDTO>> violations = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testAuthenticate_Success() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1L);
        userAccount.setEmail("test@email.com");
        userAccount.setUsername("testUser");

        UserAccountDTO request = new UserAccountDTO("test@email.com", "testUser", "password123", "password123");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(userAccount));

        when(jwtService.generateToken(userAccount)).thenReturn("mocked-jwt-token");

        AuntResponse response = userAccountService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("testUser", response.getUsername());
        assertEquals("test@email.com", response.getEmail());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(userAccount);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        UserAccountDTO request = new UserAccountDTO("notfound@email.com", "user", "password", "password");

        when(userAccountRepository.findUserAccountByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userAccountService.authenticate(request));

        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testAuthenticate_WrongPassword() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1L);
        userAccount.setEmail("test@email.com");
        userAccount.setUsername("testUser");

        UserAccountDTO request = new UserAccountDTO("test@email.com", "testUser", "wrongPassword", "wrongPassword");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(userAccount));

        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(RuntimeException.class, () -> userAccountService.authenticate(request));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testRecoverPassword_Success() {
        UserAccount userAccount = new UserAccount();
        userAccount.setPassword("oldEncodedPassword");

        PasswordDTO passwordDTO = new PasswordDTO("oldPassword", "newPassword", "newPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        boolean result = userAccountService.recoverPassword(passwordDTO, userAccount);

        assertTrue(result);
        assertEquals("encodedNewPassword", userAccount.getPassword());
        verify(userAccountRepository, times(1)).save(userAccount);
    }

    @Test
    void testRecoverPassword_Fail_PasswordsDoNotMatch() {
        UserAccount userAccount = new UserAccount();
        userAccount.setPassword("oldEncodedPassword");

        PasswordDTO passwordDTO = new PasswordDTO("oldPassword", "newPassword", "wrongPassword");

        boolean result = userAccountService.recoverPassword(passwordDTO, userAccount);

        assertFalse(result);
        assertEquals("oldEncodedPassword", userAccount.getPassword());
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void testChangerPassword_Success() {
        // Arrange
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail("test@email.com");
        userAccount.setPassword("encodedOldPassword");

        PasswordDTO passwordDTO = new PasswordDTO("oldPassword", "newPassword", "newPassword");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(userAccount));

        when(jwtService.isTokenValid("validToken", userAccount)).thenReturn(true);
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userAccountService.changerPassword(passwordDTO, "test@email.com", "validToken");

        assertEquals("encodedNewPassword", userAccount.getPassword());
        verify(userAccountRepository, times(1)).save(userAccount);
    }

    @Test
    void testChangerPassword_Fail_UserNotFound() {
        PasswordDTO passwordDTO = new PasswordDTO("oldPassword", "newPassword", "newPassword");

        when(userAccountRepository.findUserAccountByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> userAccountService.changerPassword(passwordDTO, "notfound@email.com", "validToken"));

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void testChangerPassword_Fail_InvalidToken() {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail("test@email.com");
        userAccount.setPassword("encodedOldPassword");

        PasswordDTO passwordDTO = new PasswordDTO("oldPassword", "newPassword", "newPassword");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(userAccount));

        when(jwtService.isTokenValid("invalidToken", userAccount)).thenReturn(false);

        assertThrows(UsernameNotFoundException.class,
                () -> userAccountService.changerPassword(passwordDTO, "test@email.com", "invalidToken"));

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void testChangerPassword_Fail_WrongOldPassword() {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail("test@email.com");
        userAccount.setPassword("encodedOldPassword");

        PasswordDTO passwordDTO = new PasswordDTO("wrongOldPassword", "newPassword", "newPassword");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(userAccount));

        when(jwtService.isTokenValid("validToken", userAccount)).thenReturn(true);
        when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class,
                () -> userAccountService.changerPassword(passwordDTO, "test@email.com", "validToken"));

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void testFindUserNamesById_BothUsersExist() {
        UserAccount user1 = new UserAccount();
        user1.setId(1L);
        user1.setUsername("UserOne");

        UserAccount user2 = new UserAccount();
        user2.setId(2L);
        user2.setUsername("UserTwo");

        when(userAccountRepository.findUserAccountById(1L)).thenReturn(Optional.of(user1));
        when(userAccountRepository.findUserAccountById(2L)).thenReturn(Optional.of(user2));

        List<String> result = userAccountService.findUserNamesById("1", "2");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("UserOne", result.get(0));
        assertEquals("UserTwo", result.get(1));
    }

    @Test
    void testFindUserNamesById_OneUserMissing() {
        UserAccount user1 = new UserAccount();
        user1.setId(1L);
        user1.setUsername("UserOne");

        when(userAccountRepository.findUserAccountById(1L)).thenReturn(Optional.of(user1));
        when(userAccountRepository.findUserAccountById(2L)).thenReturn(Optional.empty());

        List<String> result = userAccountService.findUserNamesById("1", "2");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindUserNamesById_BothUsersMissing() {
        when(userAccountRepository.findUserAccountById(1L)).thenReturn(Optional.empty());
        when(userAccountRepository.findUserAccountById(2L)).thenReturn(Optional.empty());

        List<String> result = userAccountService.findUserNamesById("1", "2");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindUserAccountByUserName_Success() {
        UserAccount user1 = new UserAccount();
        user1.setId(1L);
        user1.setUsername("UserOne");

        UserAccount user2 = new UserAccount();
        user2.setId(2L);
        user2.setUsername("UserTwo");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(user1));

        when(jwtService.isTokenValid("validToken", user1)).thenReturn(true);

        when(userAccountRepository.findTop10UserAccountByUsernameStartingWith("User"))
                .thenReturn(List.of(user1, user2));

        List<UserInfo> result = userAccountService.findUserAccountByUserName("User", "validToken", "test@email.com");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("UserOne", result.get(0).getUsername());
        assertEquals(2L, result.get(1).getId());
        assertEquals("UserTwo", result.get(1).getUsername());
    }

    @Test
    void testFindUserAccountByUserName_UserNotFound() {
        when(userAccountRepository.findUserAccountByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        List<UserInfo> result = userAccountService.findUserAccountByUserName("User", "validToken", "notfound@email.com");

        assertNull(result);
    }

    @Test
    void testFindUserAccountByUserName_InvalidUsernameLength() {
        UserAccount user1 = new UserAccount();
        user1.setId(1L);
        user1.setUsername("UserOne");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(user1));

        when(jwtService.isTokenValid("validToken", user1)).thenReturn(true);

        List<UserInfo> result = userAccountService.findUserAccountByUserName("Usa", "validToken", "test@email.com");

        assertTrue(result.isEmpty());
    }


    @Test
    void testFindUserAccountByUserName_InvalidToken() {
        UserAccount user1 = new UserAccount();
        user1.setId(1L);
        user1.setUsername("UserOne");

        when(userAccountRepository.findUserAccountByEmail("test@email.com"))
                .thenReturn(Optional.of(user1));

        when(jwtService.isTokenValid("invalidToken", user1)).thenReturn(false);

        List<UserInfo> result = userAccountService.findUserAccountByUserName("User", "invalidToken", "test@email.com");

        assertNull(result);
    }
}
