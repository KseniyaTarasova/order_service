package by.innowise.order_service.dto;

import by.innowise.order_service.entity.OrderStatus;

import java.util.List;

public record OrderFilterDto(
        Long userId,
        List<OrderStatus> statuses,
        List<Long> ordersIds
) {
}
