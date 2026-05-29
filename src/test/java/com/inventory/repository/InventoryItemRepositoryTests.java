package com.inventory.repository;

import com.inventory.model.InventoryItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryItemRepositoryTests {

    @Autowired
    private InventoryItemRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveItem_shouldReturnSavedItemWithId() {
        InventoryItem item = new InventoryItem();
        item.setName("Test Item");
        item.setQuantity(10);

        InventoryItem savedItem = repository.save(item);

        assertNotNull(savedItem.getId());
        assertEquals("Test Item", savedItem.getName());
        assertEquals(10, savedItem.getQuantity());
    }

    @Test
    void findById_shouldReturnItemWhenExists() {
        InventoryItem item = new InventoryItem();
        item.setName("Find Me");
        item.setQuantity(5);
        InventoryItem savedItem = repository.save(item);

        Optional<InventoryItem> foundItem = repository.findById(savedItem.getId());

        assertTrue(foundItem.isPresent());
        assertEquals("Find Me", foundItem.get().getName());
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenNotFound() {
        Optional<InventoryItem> foundItem = repository.findById(999L);
        assertFalse(foundItem.isPresent());
    }

    @Test
    void deleteItem_shouldRemoveItemFromRepository() {
        InventoryItem item = new InventoryItem();
        item.setName("Delete Me");
        item.setQuantity(1);
        InventoryItem savedItem = repository.save(item);
        Long itemId = savedItem.getId();

        repository.delete(savedItem);
        entityManager.flush(); // force deletion to DB

        Optional<InventoryItem> deletedItem = repository.findById(itemId);
        assertFalse(deletedItem.isPresent());
    }

    @Test
    void saveNullEntity_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> repository.save(null));
    }
}