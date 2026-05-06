package com.internship.tool.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    @Test
    void tokenRoundTripIncludesEmailAndRole() {
        JwtUtil jwtUtil = new JwtUtil("mysecretkeymysecretkeymysecretkey", 3600);

        String token = jwtUtil.generateToken("user@example.com", "role_admin");

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("user@example.com", jwtUtil.extractEmail(token));
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void invalidTokenReturnsFalse() {
        JwtUtil jwtUtil = new JwtUtil("mysecretkeymysecretkeymysecretkey", 3600);

        assertFalse(jwtUtil.validateToken("invalid-token"));
    }
}
