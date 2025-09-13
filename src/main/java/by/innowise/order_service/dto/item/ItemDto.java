package by.innowise.order_service.dto.item;

import java.math.BigDecimal;

public record ItemDto(
        Long id,
        String name,
        BigDecimal price,
        int quantity
) {
}
