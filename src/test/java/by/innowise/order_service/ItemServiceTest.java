package by.innowise.order_service;

import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.dto.item.CreateItemDto;
import by.innowise.order_service.dto.item.ItemDto;
import by.innowise.order_service.dto.item.UpdateItemDto;
import by.innowise.order_service.entity.Item;
import by.innowise.order_service.entity.OrderItem;
import by.innowise.order_service.exception.InsufficientQuantityException;
import by.innowise.order_service.mapper.ItemMapper;
import by.innowise.order_service.repository.ItemRepository;
import by.innowise.order_service.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Test
    void createItem_shouldSaveAndReturnDto() {
        CreateItemDto createDto = new CreateItemDto("Item1", 10, 100.0);
        Item item = new Item();
        Item savedItem = new Item();
        ItemDto itemDto = new ItemDto();

        when(itemMapper.toEntity(createDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.toDto(savedItem)).thenReturn(itemDto);

        ItemDto result = itemService.createItem(createDto);

        assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
        verify(itemMapper, times(1)).toDto(savedItem);
    }

    @Test
    void getItemDtoById_shouldReturnDto() {
        Item item = new Item();
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.getItemDtoById(1L);

        assertNotNull(result);
        verify(itemRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    void updateItem_shouldUpdateAndReturnDto() {
        UpdateItemDto updateDto = new UpdateItemDto("Updated", 5, 50.0);
        Item item = new Item();
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(itemMapper).update(updateDto, item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(1L, updateDto);

        assertNotNull(result);
        verify(itemMapper, times(1)).update(updateDto, item);
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    void deleteItem_shouldCallRepositoryDelete() {
        Item item = new Item();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L);

        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void increaseItemsQuantity_shouldAddQuantities() {
        Item item = new Item();
        item.setQuantity(5);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(3);

        Item spyItem = spy(item);
        List<OrderItem> items = List.of(orderItem);

        doReturn(spyItem).when(itemService).getItemById(item.getId());

        itemService.increaseItemsQuantity(items);

        assertEquals(8, item.getQuantity());
    }

    @Test
    void decreaseItemQuantity_shouldReduceQuantity() {
        Item item = new Item();
        item.setId(1L);
        item.setQuantity(10);

        OrderItemDto dto = new OrderItemDto(1L, 3);

        doReturn(item).when(itemService).getItemById(1L);

        Item result = itemService.decreaseItemQuantity(dto);

        assertEquals(7, result.getQuantity());
    }

    @Test
    void decreaseItemQuantity_shouldThrowExceptionWhenInsufficient() {
        Item item = new Item();
        item.setId(1L);
        item.setQuantity(2);

        OrderItemDto dto = new OrderItemDto(1L, 5);

        doReturn(item).when(itemService).getItemById(1L);

        assertThrows(InsufficientQuantityException.class, () -> itemService.decreaseItemQuantity(dto));
    }
}

