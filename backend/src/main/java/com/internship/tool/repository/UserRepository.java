package com.internship.tool.repository;

import com.internship.tool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, Long> {
Optional<User> findByEmail(String email);

List<User> findByIsActiveTrue();
@Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
List<User> searchByName(@Param("keyword") String keyword);
@Query(value = "SELECT * FROM users WHERE role = :role", nativeQuery = true)
List<User> findByRoleNative(@Param("role") String role);
}
