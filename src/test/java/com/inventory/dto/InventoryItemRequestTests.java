package com.inventory.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryItemRequestTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid request should have no violations")
    void validRequest_NoViolations() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Laptop");
        request.setQuantity(10);
        request.setPrice(BigDecimal.valueOf(999.99));
        request.setDescription("A high-end laptop");

        Set<ConstraintViolation<InventoryItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no validation violations");
    }

    @Test
    @DisplayName("Blank name violates @NotBlank constraint")
    void blankName_ViolatesNotBlank() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("   "); // only whitespace
        request.setQuantity(5);
        request.setPrice(BigDecimal.ONE);

        Set<ConstraintViolation<InventoryItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItemRequest> violation = violations.iterator().next();
        assertEquals("Name is mandatory", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Null quantity violates @NotNull constraint")
    void nullQuantity_ViolatesNotNull() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Phone");
        request.setQuantity(null);
        request.setPrice(BigDecimal.TEN);

        Set<ConstraintViolation<InventoryItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItemRequest> violation = violations.iterator().next();
        assertEquals("Quantity is mandatory", violation.getMessage());
        assertEquals("quantity", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Negative quantity violates @Min constraint")
    void negativeQuantity_ViolatesMin() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Tablet");
        request.setQuantity(-1);
        request.setPrice(BigDecimal.TEN);

        Set<ConstraintViolation<InventoryItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItemRequest> violation = violations.iterator().next();
        assertEquals("Quantity must be non-negative", violation.getMessage());
        assertEquals("quantity", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Quantity of zero is valid")
    void zeroQuantity_Valid() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Chair");
        request.setQuantity(0);
        request.setPrice(BigDecimal.valueOf(49.99));

        Set<ConstraintViolation<InventoryItemRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Expected no violations for zero quantity");
    }

    @Test
    @DisplayName("Null price violates @NotNull constraint")
    void nullPrice_ViolatesNotNull() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Monitor");
        request.setQuantity(2);
        request.setPrice(null);

        Set<ConstraintViolation<InventoryItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItemRequest> violation = violations.iterator().next();
        assertEquals("Price is mandatory", violation.getMessage());
        assertEquals("price", violation.getPropertyPath().toString());
    }
}