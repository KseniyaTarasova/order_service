package by.innowise.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDto(
        @NotNull(message = "User id must not be null")
        Long userId,
        @NotBlank(message = "Status must not be empty")
        String status,
        @NotEmpty(message = "Order items must not be empty")
        List<OrderItemDto> orderItems
) {
}
