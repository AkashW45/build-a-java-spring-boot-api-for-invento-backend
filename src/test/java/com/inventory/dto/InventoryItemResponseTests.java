package com.inventory.dto;

import com.inventory.model.InventoryItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemResponseTests {

    @Test
    void fromEntity_ShouldMapAllFields() {
        InventoryItem item = new InventoryItem();
        item.setId(1L);
        item.setName("Laptop");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("999.99"));
        item.setDescription("High-end laptop");
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now.plusHours(1));

        InventoryItemResponse response = InventoryItemResponse.fromEntity(item);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(10, response.getQuantity());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        assertEquals("High-end laptop", response.getDescription());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now.plusHours(1), response.getUpdatedAt());
    }

    @Test
    void fromEntity_WithNullInput_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> InventoryItemResponse.fromEntity(null));
    }

    @Test
    void fromEntity_WithNullFields_ShouldReturnResponseWithNulls() {
        InventoryItem item = new InventoryItem();
        // all fields are null

        InventoryItemResponse response = InventoryItemResponse.fromEntity(item);

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getQuantity());
        assertNull(response.getPrice());
        assertNull(response.getDescription());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void fromEntity_ShouldNotModifyOriginalEntity() {
        InventoryItem item = new InventoryItem();
        item.setId(2L);
        item.setName("Mouse");
        item.setQuantity(5);
        item.setPrice(new BigDecimal("25.50"));
        item.setDescription("Wireless mouse");
        LocalDateTime created = LocalDateTime.of(2023, 1, 1, 10, 0);
        item.setCreatedAt(created);
        item.setUpdatedAt(created.plusDays(1));

        InventoryItemResponse.fromEntity(item);

        // Original entity remains unchanged
        assertEquals(2L, item.getId());
        assertEquals("Mouse", item.getName());
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("25.50"), item.getPrice());
        assertEquals("Wireless mouse", item.getDescription());
        assertEquals(created, item.getCreatedAt());
        assertEquals(created.plusDays(1), item.getUpdatedAt());
    }

    @Test
    void fromEntity_WithEmptyStringsAndZeroQuantity_ShouldMapCorrectly() {
        InventoryItem item = new InventoryItem();
        item.setId(3L);
        item.setName("");
        item.setQuantity(0);
        item.setPrice(BigDecimal.ZERO);
        item.setDescription("");
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);

        InventoryItemResponse response = InventoryItemResponse.fromEntity(item);

        assertEquals(3L, response.getId());
        assertEquals("", response.getName());
        assertEquals(0, response.getQuantity());
        assertEquals(BigDecimal.ZERO, response.getPrice());
        assertEquals("", response.getDescription());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
}