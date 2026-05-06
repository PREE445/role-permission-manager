package com.internship.tool.config;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataSeederTest {

    @Test
    void seedsThirtyUsersWhenDatabaseIsShort() throws Exception {
        UserRepository repository = mock(UserRepository.class);
        when(repository.count()).thenReturn(0L);
        when(repository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        CommandLineRunner runner = new DataSeeder().seedUsers(repository);
        runner.run();

        verify(repository, atLeast(30)).save(any(User.class));
    }

    @Test
    void skipsWhenDatabaseAlreadyHasThirtyUsers() throws Exception {
        UserRepository repository = mock(UserRepository.class);
        when(repository.count()).thenReturn(30L);

        new DataSeeder().seedUsers(repository).run();

        verify(repository, never()).save(any(User.class));
    }
}
