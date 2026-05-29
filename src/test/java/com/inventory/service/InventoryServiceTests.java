package com.inventory.service;

import com.inventory.dto.InventoryItemRequest;
import com.inventory.dto.InventoryItemResponse;
import com.inventory.model.InventoryItem;
import com.inventory.repository.InventoryItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTests {

    @Mock
    private InventoryItemRepository repository;

    @InjectMocks
    private InventoryService service;

    @Test
    void findAll_ReturnsListOfItems() {
        InventoryItem item1 = createItem(1L, "Item1", 10, BigDecimal.TEN, "Desc1");
        InventoryItem item2 = createItem(2L, "Item2", 20, BigDecimal.ONE, "Desc2");
        when(repository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<InventoryItemResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Item1");
        assertThat(result.get(1).getName()).isEqualTo("Item2");
        verify(repository).findAll();
    }

    @Test
    void findById_ExistingId_ReturnsItem() {
        InventoryItem item = createItem(1L, "Test", 5, BigDecimal.valueOf(9.99), "desc");
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        InventoryItemResponse response = service.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test");
        assertThat(response.getQuantity()).isEqualTo(5);
        verify(repository).findById(1L);
    }

    @Test
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(99L));
        verify(repository).findById(99L);
    }

    @Test
    void create_ValidRequest_SavesAndReturnsItem() {
        InventoryItemRequest request = new InventoryItemRequest("New", 3, BigDecimal.valueOf(5.5), "new desc");
        InventoryItem savedItem = new InventoryItem();
        savedItem.setId(1L);
        savedItem.setName("New");
        savedItem.setQuantity(3);
        savedItem.setPrice(BigDecimal.valueOf(5.5));
        savedItem.setDescription("new desc");
        when(repository.save(any(InventoryItem.class))).thenReturn(savedItem);

        InventoryItemResponse response = service.create(request);

        assertThat(response.getName()).isEqualTo("New");
        assertThat(response.getQuantity()).isEqualTo(3);
        verify(repository).save(any(InventoryItem.class));
    }

    @Test
    void update_NonExistingId_ThrowsEntityNotFoundException() {
        InventoryItemRequest request = new InventoryItemRequest("X", 1, BigDecimal.ONE, "x");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(99L, request));
        verify(repository).findById(99L);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_NonExistingId_ThrowsEntityNotFoundException() {
        when(repository.existsById(100L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.delete(100L));
        verify(repository).existsById(100L);
        verify(repository, never()).deleteById(anyLong());
    }

    private InventoryItem createItem(Long id, String name, int quantity, BigDecimal price, String description) {
        InventoryItem item = new InventoryItem();
        item.setId(id);
        item.setName(name);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setDescription(description);
        return item;
    }
}