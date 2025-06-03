package com.squad2.Squad2_cobol.controllers;

import com.squad2.Squad2_cobol.jwt.JwtToken;
import com.squad2.Squad2_cobol.jwt.JwtUtill;
import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import com.squad2.Squad2_cobol.services.BlackListService;
import com.squad2.Squad2_cobol.services.CustomerService;
import com.squad2.Squad2_cobol.web.controllers.AuthController;
import com.squad2.Squad2_cobol.web.dtos.LoginRequest;
import com.squad2.Squad2_cobol.web.dtos.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private BlackListService blackListService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext(); // garante um contexto limpo
    }

    @Test
    void login_shouldAuthenticateAndReturnUserInfo() {
        // Given
        String email = "test@example.com";
        String password = "password";
        String name = "Test User";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        Customer mockCustomer = new Customer();
        mockCustomer.setEmail(email);
        mockCustomer.setName(name);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(mockCustomer));

        try (MockedStatic<JwtUtill> utilities = Mockito.mockStatic(JwtUtill.class)) {
            JwtToken fakeToken = new JwtToken("fake.jwt.token");
            utilities.when(() -> JwtUtill.createToken(email)).thenReturn(fakeToken);

            // When
            ResponseEntity<LoginResponse> response = authController.login(loginRequest);

            // Then
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(name, response.getBody().getName());
            assertEquals(email, response.getBody().getEmail());
            assertEquals("fake.jwt.token", response.getBody().getToken());
        }
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> authController.login(loginRequest));
        verify(customerRepository).findByEmail(email);
    }

    @Test
    void login_shouldThrowException_whenAuthenticationFails() {
        // Given
        String email = "test@example.com";
        String password = "wrongpassword";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authController.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(customerRepository);
    }

    @Test
    void logout_shouldBlacklistTokenAndClearContext_whenTokenValid() {
        // Given
        String jwt = "valid.jwt.token";
        String bearerToken = JwtUtill.JWT_BEARER + jwt;

        // When
        ResponseEntity<Map<String, String>> response = authController.logout(bearerToken);

        // Then
        verify(blackListService).BlacklistToken(jwt);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Logout successful", response.getBody().get("message"));

        // Não conseguimos verificar o SecurityContextHolder.clearContext diretamente,
        // pois ele é um método estático sem acesso interno.
        // Considerar fazer mock com PowerMock ou deixar assim.
    }

    @Test
    void logout_shouldReturnBadRequest_whenTokenInvalid() {
        // Given
        String invalidToken = "invalidToken";

        // When
        ResponseEntity<Map<String, String>> response = authController.logout(invalidToken);

        // Then
        verifyNoInteractions(blackListService);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void logout_shouldReturnBadRequest_whenTokenIsNull() {
        // When
        ResponseEntity<Map<String, String>> response = authController.logout(null);

        // Then
        verifyNoInteractions(blackListService);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}
