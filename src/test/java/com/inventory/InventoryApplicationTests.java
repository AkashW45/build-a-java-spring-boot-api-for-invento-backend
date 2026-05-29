package com.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryApplicationTests {

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class ContextLoadingTests {
        @Test
        void contextLoads() {
            // Spring context loads successfully
        }
    }

    @Test
    void main_ShouldCallRunWithCorrectClassAndArgs() {
        try (MockedStatic<SpringApplication> mockStatic = mockStatic(SpringApplication.class)) {
            String[] args = {"--server.port=0"};
            InventoryApplication.main(args);
            mockStatic.verify(() -> SpringApplication.run(eq(InventoryApplication.class), eq(args)));
        }
    }

    @Test
    void main_WithNullArgs_ShouldCallRunWithNull() {
        try (MockedStatic<SpringApplication> mockStatic = mockStatic(SpringApplication.class)) {
            InventoryApplication.main(null);
            mockStatic.verify(() -> SpringApplication.run(eq(InventoryApplication.class), isNull()));
        }
    }

    @Test
    void main_WithEmptyArgs_ShouldCallRunWithEmpty() {
        try (MockedStatic<SpringApplication> mockStatic = mockStatic(SpringApplication.class)) {
            String[] args = new String[0];
            InventoryApplication.main(args);
            mockStatic.verify(() -> SpringApplication.run(eq(InventoryApplication.class), eq(args)));
        }
    }

    @Test
    void main_WhenRunThrowsException_ShouldPropagate() {
        RuntimeException exception = new RuntimeException("Startup failure");
        try (MockedStatic<SpringApplication> mockStatic = mockStatic(SpringApplication.class)) {
            mockStatic.when(() -> SpringApplication.run(eq(InventoryApplication.class), any()))
                      .thenThrow(exception);
            assertThrows(RuntimeException.class, () -> InventoryApplication.main(new String[]{}));
        }
    }
}