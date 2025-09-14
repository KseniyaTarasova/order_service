package by.innowise.order_service;

import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.dto.UserDto;
import by.innowise.order_service.dto.order.CreateOrderDto;
import by.innowise.order_service.dto.order.OrderDto;
import by.innowise.order_service.dto.order.UpdateOrderDto;
import by.innowise.order_service.entity.Item;
import by.innowise.order_service.entity.Order;
import by.innowise.order_service.entity.OrderItem;
import by.innowise.order_service.entity.OrderStatus;
import by.innowise.order_service.mapper.OrderMapper;
import by.innowise.order_service.repository.OrderRepository;
import by.innowise.order_service.service.ItemService;
import by.innowise.order_service.service.OrderService;
import by.innowise.order_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;
    @Mock
    private OrderMapper orderMapper;

    private final String token = "test-token";

    @Test
    void createOrder_shouldSaveOrderAndReturnDto() {
        CreateOrderDto createDto = new CreateOrderDto(1L, List.of(new OrderItemDto(1L, 2)));

        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);
        order.setUserId(1L);

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setStatus(OrderStatus.PROCESSING);
        savedOrder.setUserId(1L);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(100L);

        when(itemService.decreaseItemQuantity(any())).thenReturn(new Item());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(orderDto);
        when(userService.getUserByEmail(token)).thenReturn(UserDto.builder()
                .id(1L)
                .email("test@mail.com")
                .build());

        OrderDto result = orderService.createOrder(createDto, token);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(itemService, times(createDto.orderItems().size())).decreaseItemQuantity(any());
    }

    @Test
    void getOrderDtoById_shouldReturnOrderDto() {
        Order order = new Order();
        order.setId(1L);

        OrderDto dto = new OrderDto();
        dto.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);
        when(userService.getUserByEmail(token)).thenReturn(UserDto.builder()
                .id(1L)
                .email("test@mail.com")
                .build());

        OrderDto result = orderService.getOrderDtoById(1L, token);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deleteOrder_shouldCallRepositoryDelete() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(List.of(new OrderItem()));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(itemService, times(order.getOrderItems().size())).increaseItemsQuantity(order.getOrderItems());
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void updateOrder_shouldUpdateItemsAndReturnDto() {
        UpdateOrderDto updateDto = new UpdateOrderDto(List.of(new OrderItemDto(1L, 3)));
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(new ArrayList<>());

        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(itemService.decreaseItemQuantity(any())).thenReturn(new Item());
        when(orderMapper.toDto(order)).thenReturn(orderDto);
        when(userService.getUserByEmail(token)).thenReturn(UserDto.builder()
                .id(1L)
                .email("test@mail.com")
                .build());

        OrderDto result = orderService.updateOrder(1L, updateDto, token);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, order.getOrderItems().size());
    }
}

