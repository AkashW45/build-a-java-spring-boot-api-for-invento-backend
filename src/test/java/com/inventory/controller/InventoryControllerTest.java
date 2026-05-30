package com.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.dto.InventoryItemRequest;
import com.inventory.dto.InventoryItemResponse;
import com.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateItem() throws Exception {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Test");
        request.setQuantity(5);
        request.setPrice(BigDecimal.TEN);

        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        response.setName("Test");
        response.setQuantity(5);
        response.setPrice(BigDecimal.TEN);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void testGetAllItems() throws Exception {
        InventoryItemResponse item = new InventoryItemResponse();
        item.setId(1L);
        item.setName("Item1");
        item.setQuantity(10);
        item.setPrice(BigDecimal.valueOf(19.99));

        when(service.findAll()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @TestConfiguration
    static class LoggingFilterConfig {

        private static final Logger logger = LoggerFactory.getLogger("RequestLogging");
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Bean
        public OncePerRequestFilter loggingFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain chain)
                        throws ServletException, IOException {
                    long start = System.currentTimeMillis();
                    chain.doFilter(request, response);
                    long duration = System.currentTimeMillis() - start;
                    String timestamp = LocalDateTime.now().format(formatter);
                    logger.info("{} {} {} {} {}ms",
                            timestamp,
                            request.getMethod(),
                            request.getRequestURI(),
                            response.getStatus(),
                            duration);
                }
            };
        }
    }
}
