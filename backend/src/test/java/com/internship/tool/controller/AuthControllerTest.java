package com.internship.tool.controller;

import com.internship.tool.config.JwtUtil;
import com.internship.tool.entity.User;
import com.internship.tool.exception.InvalidInputException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private UserRepository userRepository;
    private UserService userService;
    private JwtUtil jwtUtil;
    private AuthController controller;
    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        jwtUtil = new JwtUtil("mysecretkeymysecretkeymysecretkey", 3600);
        controller = new AuthController(userRepository, userService, jwtUtil);

        user = new User();
        user.setName("Admin");
        user.setEmail("admin@example.com");
        user.setPassword("pass");
        user.setRole("ADMIN");
        user.setIsActive(true);
    }

    @Test
    void registerReturnsCreatedUser() {
        when(userService.createUser(user)).thenReturn(user);

        assertEquals(HttpStatus.CREATED, controller.register(user).getStatusCode());
    }

    @Test
    void loginReturnsRoleAwareToken() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        String token = controller.login(user).getBody();

        assertEquals("admin@example.com", jwtUtil.extractEmail(token));
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void loginRejectsMissingUnknownAndWrongPassword() {
        user.setEmail("");
        assertThrows(InvalidInputException.class, () -> controller.login(user));

        user.setEmail("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> controller.login(user));

        user.setEmail("admin@example.com");
        user.setPassword("bad");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(createStoredUser()));
        assertThrows(InvalidInputException.class, () -> controller.login(user));
    }

    @Test
    void refreshReturnsNewTokenWithSameRole() {
        String token = jwtUtil.generateToken("admin@example.com", "ADMIN");
        String refreshed = controller.refresh("Bearer " + token).getBody();

        assertEquals("admin@example.com", jwtUtil.extractEmail(refreshed));
        assertEquals("ADMIN", jwtUtil.extractRole(refreshed));
    }

    @Test
    void refreshRejectsBadHeader() {
        assertThrows(InvalidInputException.class, () -> controller.refresh("bad"));
    }

    private User createStoredUser() {
        User stored = new User();
        stored.setEmail("admin@example.com");
        stored.setPassword("pass");
        stored.setRole("ADMIN");
        return stored;
    }
}
