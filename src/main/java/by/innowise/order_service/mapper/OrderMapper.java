package by.innowise.order_service.mapper;

import by.innowise.order_service.dto.order.OrderDto;
import by.innowise.order_service.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {OrderItemMapper.class}
)
public interface OrderMapper {
    OrderDto toDto(Order order);
}
