package by.innowise.order_service.mapper;

import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "itemId", source = "item.id")
    OrderItemDto toDto(OrderItem orderItem);
}
