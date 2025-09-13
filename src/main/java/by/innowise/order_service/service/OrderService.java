package by.innowise.order_service.service;

import by.innowise.order_service.dto.OrderFilterDto;
import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.dto.UserDto;
import by.innowise.order_service.dto.order.CreateOrderDto;
import by.innowise.order_service.dto.order.OrderDto;
import by.innowise.order_service.dto.order.UpdateOrderDto;
import by.innowise.order_service.entity.Item;
import by.innowise.order_service.entity.Order;
import by.innowise.order_service.entity.OrderItem;
import by.innowise.order_service.entity.OrderStatus;
import by.innowise.order_service.exception.OrderNotFoundException;
import by.innowise.order_service.exception.UserNotFoundException;
import by.innowise.order_service.util.OrderSpecification;
import by.innowise.order_service.mapper.OrderMapper;
import by.innowise.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final ItemService itemService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    public OrderDto createOrder(CreateOrderDto dto, String token) {
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);
        order.setUserId(dto.userId());

        addItemsToOrder(order, dto.orderItems());

        Order savedOrder = orderRepository.save(order);

        return setUser(orderMapper.toDto(savedOrder), token);
    }

    @Transactional
    public OrderDto updateOrder(Long id, UpdateOrderDto dto, String token) {
        Order order = getOrderById(id);

        itemService.increaseItemsQuantity(order.getOrderItems());
        order.getOrderItems().clear();

        addItemsToOrder(order, dto.orderItems());

        return setUser(orderMapper.toDto(order), token);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderDtoById(Long id, String token) {
        Order order = getOrderById(id);

        return setUser(orderMapper.toDto(order), token);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);

        itemService.increaseItemsQuantity(order.getOrderItems());

        orderRepository.delete(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersWithFilters(OrderFilterDto filters, Pageable pageable, String token) {
        return orderRepository.findAll(OrderSpecification.withFilter(filters), pageable)
                .map(orderMapper::toDto)
                .map(orderDto -> setUser(orderDto, token));
    }

    private UserDto getUserByEmail(String token) {
        UserDto user = userService.getUserByEmail(token);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    private OrderDto setUser(OrderDto orderDto, String token) {
        UserDto user = getUserByEmail(token);
        orderDto.setUser(user);
        return orderDto;
    }

    private void addItemsToOrder(Order order, List<OrderItemDto> orderItems) {
        orderItems.forEach(orderItemDto -> {
            Item item = itemService.decreaseItemQuantity(orderItemDto);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantity(orderItemDto.quantity());
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);
        });
    }
}
