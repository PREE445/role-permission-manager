package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.exception.InvalidInputException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("Test@Mail.com");
        user.setPassword("123");
        user.setRole("user");
        user.setIsActive(true);
    }

    @Test
    void createUserNormalizesAndSaves() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertEquals("test@mail.com", result.getEmail());
        assertEquals("USER", result.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void createUserRejectsMissingEmailDuplicateEmailAndBadRole() {
        user.setEmail("");
        assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        user.setEmail("test@mail.com");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        user.setEmail("new@mail.com");
        user.setRole("MANAGER");
        assertThrows(InvalidInputException.class, () -> userService.createUser(user));
    }

    @Test
    void getUserByIdReturnsUserOrThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals("Test User", userService.getUserById(1L).getName());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void getAllUsersReturnsPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        assertEquals(1, userService.getAllUsers(pageable).getTotalElements());
    }

    @Test
    void updateUserUpdatesFieldsAndRejectsDuplicateEmail() {
        User update = new User();
        update.setName("Updated");
        update.setEmail("updated@mail.com");
        update.setPassword("newpass");
        update.setRole("ADMIN");
        update.setIsActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, update);

        assertEquals("Updated", result.getName());
        assertEquals("updated@mail.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        assertEquals(false, result.getIsActive());

        User other = new User();
        other.setId(99L);
        when(userRepository.findByEmail("updated@mail.com")).thenReturn(Optional.of(other));
        assertThrows(InvalidInputException.class, () -> userService.updateUser(1L, update));
    }

    @Test
    void deleteUserDeletesExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void createUserDefaultsActiveStatusAndRole() {
        user.setRole(null);
        user.setIsActive(null);
        user.setEmail("default@mail.com");
        when(userRepository.findByEmail("default@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertEquals("USER", result.getRole());
        assertTrue(result.getIsActive());
    }
}
