package com.inventory.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testEntityNotFoundException_ReturnsNotFoundWithMessage() {
        EntityNotFoundException ex = new EntityNotFoundException("Item not found");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals("Item not found", body.get("error"));
    }

    @Test
    void testEntityNotFoundException_NullMessage_ReturnsNotFoundWithNullError() {
        EntityNotFoundException ex = new EntityNotFoundException(null);
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals(1, body.size());
        assertTrue(body.containsKey("error"));
        assertEquals(null, body.get("error"));
    }

    @Test
    void testMethodArgumentNotValidException_SingleFieldError_ReturnsBadRequestWithErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        FieldError fieldError = new FieldError("inventory", "name", "must not be blank");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals("must not be blank", body.get("name"));
    }

    @Test
    void testMethodArgumentNotValidException_MultipleFieldErrors_ReturnsBadRequestWithAllErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        FieldError fieldError1 = new FieldError("inventory", "name", "must not be blank");
        FieldError fieldError2 = new FieldError("inventory", "price", "must be positive");
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals(2, body.size());
        assertEquals("must not be blank", body.get("name"));
        assertEquals("must be positive", body.get("price"));
    }

    @Test
    void testMethodArgumentNotValidException_NoFieldErrors_ReturnsBadRequestWithEmptyMap() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertTrue(body.isEmpty());
    }

    @Test
    void testGeneralException_ReturnsInternalServerErrorWithFixedMessage() {
        Exception ex = new RuntimeException("Some unexpected error");
        ResponseEntity<Map<String, String>> response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals("Internal server error", body.get("error"));
    }
}