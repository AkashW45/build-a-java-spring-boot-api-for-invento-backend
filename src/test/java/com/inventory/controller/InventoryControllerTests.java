package com.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.dto.InventoryItemRequest;
import com.inventory.dto.InventoryItemResponse;
import com.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@WebMvcTest(controllers = InventoryController.class)
class InventoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreatedAndItem() throws Exception {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Test Item");
        request.setQuantity(10);

        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        response.setName("Test Item");
        response.setQuantity(10);

        when(service.create(any(InventoryItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.quantity").value(10));

        verify(service).create(any(InventoryItemRequest.class));
    }

    @Test
    void create_ShouldReturnBadRequestWhenInvalid() throws Exception {
        InventoryItemRequest request = new InventoryItemRequest(); // missing all required fields

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(InventoryItemRequest.class));
    }

    @Test
    void getAll_ShouldReturnAllItems() throws Exception {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        response.setName("Item");
        when(service.findAll()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    void getById_ShouldReturnItem() throws Exception {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_ShouldReturn500WhenServiceFails() throws Exception {
        when(service.findById(999L)).thenThrow(new RuntimeException("Item not found"));

        mockMvc.perform(get("/api/inventory/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void update_ShouldReturnUpdatedItem() throws Exception {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Updated Item");
        request.setQuantity(20);

        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        response.setName("Updated Item");
        response.setQuantity(20);

        when(service.update(eq(1L), any(InventoryItemRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void update_ShouldReturn500WhenServiceFails() throws Exception {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Update");
        when(service.update(eq(999L), any(InventoryItemRequest.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/inventory/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/inventory/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void delete_ShouldReturn500WhenServiceFails() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(service).delete(999L);

        mockMvc.perform(delete("/api/inventory/999"))
                .andExpect(status().isInternalServerError());
    }

    @Configuration
    static class LoggingFilterConfig {
        @Bean
        public Filter requestLoggingFilter() {
            return (Filter) (request, response, chain) -> {
                HttpServletRequest httpReq = (HttpServletRequest) request;
                HttpServletResponse httpRes = (HttpServletResponse) response;
                long startTime = System.currentTimeMillis();
                chain.doFilter(request, response);
                long duration = System.currentTimeMillis() - startTime;
                String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                        .withZone(ZoneId.systemDefault())
                        .format(Instant.now());
                Logger log = LoggerFactory.getLogger("com.inventory.filters.RequestLoggingFilter");
                log.info("{} {} {} {} {}ms", timestamp, httpReq.getMethod(), httpReq.getRequestURI(),
                        httpRes.getStatus(), duration);
            };
        }
    }
}