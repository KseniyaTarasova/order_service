package by.innowise.order_service.dto.item;

import java.math.BigDecimal;

public record ItemFilterDto(
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minQuantity,
        Integer maxQuantity
) {
}
