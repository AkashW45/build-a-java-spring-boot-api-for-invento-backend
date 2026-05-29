package com.inventory.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryItemTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidItem_NoViolations() {
        InventoryItem item = new InventoryItem("Laptop", 10, new BigDecimal("999.99"), "High-end gaming laptop");
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);
        assertTrue(violations.isEmpty(), "Expected no validation violations for a valid item");
    }

    @Test
    void testLifecycleCallbacks() throws Exception {
        InventoryItem item = new InventoryItem("Phone", 5, new BigDecimal("599.00"), "Smartphone");

        // simulate @PrePersist
        java.lang.reflect.Method onCreate = InventoryItem.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(item);

        assertNotNull(item.getCreatedAt(), "createdAt should be set by onCreate");
        assertNotNull(item.getUpdatedAt(), "updatedAt should be set by onCreate");
        assertEquals(item.getCreatedAt(), item.getUpdatedAt(), "createdAt and updatedAt should be equal after create");

        LocalDateTime createTime = item.getCreatedAt();

        // simulate passage of time and @PreUpdate
        Thread.sleep(100); // ensure time difference
        java.lang.reflect.Method onUpdate = InventoryItem.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(item);

        assertNotNull(item.getUpdatedAt(), "updatedAt should still be non-null after onUpdate");
        assertTrue(item.getUpdatedAt().isAfter(createTime), "updatedAt should be after createdAt after update");
    }

    @Test
    void testQuantityZeroIsValid() {
        InventoryItem item = new InventoryItem("Empty", 0, new BigDecimal("0.00"), "No stock");
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);
        assertTrue(violations.isEmpty(), "Quantity zero should be valid");
    }

    @Test
    void testBlankName_Violation() {
        InventoryItem item = new InventoryItem("", 5, new BigDecimal("10.00"), "desc");
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItem> violation = violations.iterator().next();
        assertEquals("Name is mandatory", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testNegativeQuantity_Violation() {
        InventoryItem item = new InventoryItem("Item", -1, new BigDecimal("5.00"), "desc");
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItem> violation = violations.iterator().next();
        assertEquals("Quantity must be non-negative", violation.getMessage());
        assertEquals("quantity", violation.getPropertyPath().toString());
    }

    @Test
    void testNullPrice_Violation() {
        InventoryItem item = new InventoryItem("Item", 5, null, "desc");
        Set<ConstraintViolation<InventoryItem>> violations = validator.validate(item);
        assertEquals(1, violations.size());
        ConstraintViolation<InventoryItem> violation = violations.iterator().next();
        assertEquals("Price is mandatory", violation.getMessage());
        assertEquals("price", violation.getPropertyPath().toString());
    }
}