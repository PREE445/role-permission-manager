package com.internship.tool.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsKnownExceptionsToExpectedStatuses() {
        assertEquals(404, handler.handleNotFound(new ResourceNotFoundException("missing")).getStatusCode().value());
        assertEquals(400, handler.handleInvalid(new InvalidInputException("bad")).getStatusCode().value());
        assertEquals(403, handler.handleAccessDenied(new AccessDeniedException("denied")).getStatusCode().value());
        assertEquals(500, handler.handleGeneral(new RuntimeException("boom")).getStatusCode().value());
    }

    @Test
    void validationResponseUsesFieldMessage() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");
        bindingResult.addError(new FieldError("user", "email", "Email is required"));
        Method method = getClass().getDeclaredMethod("validationTarget", String.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Email is required", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @SuppressWarnings("unused")
    private void validationTarget(String value) {
    }
}
