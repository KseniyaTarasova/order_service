package by.innowise.order_service.dto;

import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
        @NotNull(message = "Item id must not be null")
        Long itemId,
        @NotNull(message = "Quantity must not be null")
        Integer quantity
) {
}
