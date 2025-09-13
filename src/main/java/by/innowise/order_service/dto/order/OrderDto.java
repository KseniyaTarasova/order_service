package by.innowise.order_service.dto.order;

import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.dto.UserDto;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto{
    private Long id;
    private Long userId;
    private String status;
    private String creationDate;
    private List<OrderItemDto> orderItems;
    private BigDecimal totalCost;
    private UserDto user;
}
