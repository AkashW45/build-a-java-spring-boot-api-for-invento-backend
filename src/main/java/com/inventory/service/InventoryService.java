package com.inventory.service;

import com.inventory.dto.InventoryItemRequest;
import com.inventory.dto.InventoryItemResponse;
import com.inventory.model.InventoryItem;
import com.inventory.repository.InventoryItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    private final InventoryItemRepository repository;

    public InventoryService(InventoryItemRepository repository) {
        this.repository = repository;
    }

    public List<InventoryItemResponse> findAll() {
        return repository.findAll().stream()
                .map(InventoryItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public InventoryItemResponse findById(Long id) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with id: " + id));
        return InventoryItemResponse.fromEntity(item);
    }

    public InventoryItemResponse create(InventoryItemRequest request) {
        InventoryItem item = new InventoryItem();
        item.setName(request.getName());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        item.setDescription(request.getDescription());
        InventoryItem saved = repository.save(item);
        return InventoryItemResponse.fromEntity(saved);
    }

    public InventoryItemResponse update(Long id, InventoryItemRequest request) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found with id: " + id));
        item.setName(request.getName());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        item.setDescription(request.getDescription());
        InventoryItem updated = repository.save(item);
        return InventoryItemResponse.fromEntity(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Inventory item not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
