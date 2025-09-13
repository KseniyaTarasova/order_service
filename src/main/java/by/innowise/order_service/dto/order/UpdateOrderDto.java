package by.innowise.order_service.dto.order;

import by.innowise.order_service.dto.OrderItemDto;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateOrderDto(
        @NotEmpty(message = "Order items must not be empty")
        List<OrderItemDto> orderItems
) {
}
