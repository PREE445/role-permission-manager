package com.internship.tool.controller;

import com.internship.tool.entity.User;
import com.internship.tool.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;
    private UserController controller;
    private User user;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        controller = new UserController(userService);
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");
        user.setPassword("pass");
        user.setRole("USER");
        user.setIsActive(true);
    }

    @Test
    void createGetAndListUsers() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userService.createUser(user)).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.getAllUsers(pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        assertEquals(201, controller.createUser(user).getStatusCode().value());
        assertEquals(user, controller.getUserById(1L).getBody());
        assertEquals(1, controller.getAllUsers(pageable).getBody().getTotalElements());
    }

    @Test
    void updateAndDeleteUser() {
        when(userService.updateUser(1L, user)).thenReturn(user);

        assertEquals(user, controller.updateUser(1L, user).getBody());
        assertEquals(204, controller.deleteUser(1L).getStatusCode().value());
        verify(userService).deleteUser(1L);
    }
}
