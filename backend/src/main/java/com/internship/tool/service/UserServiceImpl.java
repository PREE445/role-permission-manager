


package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.exception.InvalidInputException;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @CacheEvict(value = {"users", "usersList"}, allEntries = true)
    public User createUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidInputException("Email cannot be empty");
        }
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setRole(normalizeRole(user.getRole()));
        user.setIsActive(user.getIsActive() == null ? Boolean.TRUE : user.getIsActive());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new InvalidInputException("Email already exists");
        }

        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Cacheable(value = "usersList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @CacheEvict(value = {"users", "usersList"}, allEntries = true)
    public User updateUser(Long id, User user) {

        User existingUser = getUserById(id);

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidInputException("Email cannot be empty");
        }

        String email = user.getEmail().trim().toLowerCase();
        userRepository.findByEmail(email)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new InvalidInputException("Email already exists");
                });

        existingUser.setName(user.getName());
        existingUser.setEmail(email);
        existingUser.setPassword(user.getPassword() == null ? existingUser.getPassword() : user.getPassword());
        existingUser.setRole(normalizeRole(user.getRole()));
        existingUser.setIsActive(user.getIsActive() == null ? existingUser.getIsActive() : user.getIsActive());

        return userRepository.save(existingUser);
    }

    @Override
    @CacheEvict(value = {"users", "usersList"}, allEntries = true)
    public void deleteUser(Long id) {

        User user = getUserById(id);
        userRepository.delete(user);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        String normalized = role.replace("ROLE_", "").trim().toUpperCase();
        if (!normalized.equals("ADMIN") && !normalized.equals("USER")) {
            throw new InvalidInputException("Role must be ADMIN or USER");
        }
        return normalized;
    }
}


// package com.internship.tool.service;

// import com.internship.tool.entity.User;
// import com.internship.tool.repository.UserRepository;
// import com.internship.tool.exception.ResourceNotFoundException;
// import com.internship.tool.exception.InvalidInputException;

// import org.springframework.stereotype.Service;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import java.util.List;

// @Service
// public class UserServiceImpl implements UserService {

//     private final UserRepository userRepository;

//     public UserServiceImpl(UserRepository userRepository) {
//         this.userRepository = userRepository;
//     }

//     @Override
//     public User createUser(User user) {

//         // 🔹 Validation
//         if (user.getEmail() == null || user.getEmail().isEmpty()) {
//             throw new InvalidInputException("Email cannot be empty");
//         }

//         if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//             throw new InvalidInputException("Email already exists");
//         }

//         return userRepository.save(user);
//     }

//     @Override
//     public User getUserById(Long id) {
//         return userRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
//     }

  
    
// @Override
// public Page<User> getAllUsers(Pageable pageable) {
//     return userRepository.findAll(pageable);
// }

//     @Override
//     public User updateUser(Long id, User user) {

//         User existingUser = getUserById(id);

//         existingUser.setName(user.getName());
//         existingUser.setEmail(user.getEmail());
//         existingUser.setRole(user.getRole());
//         existingUser.setIsActive(user.getIsActive());

//         return userRepository.save(existingUser);
//     }

//     @Override
//     public void deleteUser(Long id) {

//         User user = getUserById(id);
//         userRepository.delete(user);
//     }
    
// }
