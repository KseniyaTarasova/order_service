package by.innowise.order_service.dto.item;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemDto(
        Long id,
        String name,
        BigDecimal price,
        int quantity
) {
}
