package by.innowise.order_service.service;

import by.innowise.order_service.dto.item.CreateItemDto;
import by.innowise.order_service.dto.item.ItemDto;
import by.innowise.order_service.dto.item.ItemFilterDto;
import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.dto.item.UpdateItemDto;
import by.innowise.order_service.entity.Item;
import by.innowise.order_service.entity.OrderItem;
import by.innowise.order_service.exception.InsufficientQuantityException;
import by.innowise.order_service.exception.ItemNotFoundException;
import by.innowise.order_service.mapper.ItemMapper;
import by.innowise.order_service.repository.ItemRepository;
import by.innowise.order_service.util.ItemSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    public ItemDto createItem(CreateItemDto dto) {
        Item item = itemMapper.toEntity(dto);

        Item savedItem = itemRepository.save(item);

        return itemMapper.toDto(itemRepository.save(savedItem));
    }

    @Transactional
    public ItemDto updateItem(Long id, UpdateItemDto dto) {
        Item item = getItemById(id);

        itemMapper.update(dto, item);

        return itemMapper.toDto(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = getItemById(id);

        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public ItemDto getItemDtoById(Long id) {
        Item item = getItemById(id);

        return itemMapper.toDto(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemDto> getItemsWithFilters(ItemFilterDto dto, Pageable pageable) {
        return itemRepository.findAll(ItemSpecification.withFilter(dto), pageable)
                .map(itemMapper::toDto);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + id + " not found"));
    }

    public void increaseItemsQuantity(List<OrderItem> items) {
        items.forEach(i -> {
            Item item = getItemById(i.getItem().getId());

            item.setQuantity(item.getQuantity() + i.getQuantity());
        });
    }

    public Item decreaseItemQuantity(OrderItemDto i) {
        Item item = getItemById(i.itemId());
        if (item.getQuantity() < i.quantity()) {
            throw new InsufficientQuantityException("Item with id " + i.itemId()
                    + " has quantity " + item.getQuantity()
                    + " but required quantity is " + i.quantity());
        }
        item.setQuantity(item.getQuantity() - i.quantity());
        return item;
    }
}
