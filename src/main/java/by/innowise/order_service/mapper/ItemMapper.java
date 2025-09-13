package by.innowise.order_service.mapper;

import by.innowise.order_service.dto.item.CreateItemDto;
import by.innowise.order_service.dto.item.ItemDto;
import by.innowise.order_service.dto.item.UpdateItemDto;
import by.innowise.order_service.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toEntity(CreateItemDto dto);

    ItemDto toDto(Item item);

    void update(UpdateItemDto dto, @MappingTarget Item item);
}
