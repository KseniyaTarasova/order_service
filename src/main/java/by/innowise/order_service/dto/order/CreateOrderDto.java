package by.innowise.order_service.dto.order;

import by.innowise.order_service.dto.OrderItemDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDto(
        @NotNull(message = "User id must not be null")
        Long userId,

        @NotEmpty(message = "Order items must not be empty")
        List<OrderItemDto> orderItems
) {
}
