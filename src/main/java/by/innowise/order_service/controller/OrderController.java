package by.innowise.order_service.controller;

import by.innowise.order_service.dto.OrderFilterDto;
import by.innowise.order_service.dto.order.CreateOrderDto;
import by.innowise.order_service.dto.order.OrderDto;
import by.innowise.order_service.dto.order.UpdateOrderDto;
import by.innowise.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderDto dto,
                                                @RequestHeader("Authorization") String token) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(dto, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id,
                                                @RequestBody @Valid UpdateOrderDto dto,
                                                @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(orderService.updateOrder(id, dto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String token) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(orderService.getOrderDtoById(id, token));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getAllByStatusIn(@ModelAttribute OrderFilterDto dto,
                                                           @PageableDefault(sort = "id") Pageable pageable,
                                                           @RequestHeader("Authorization") String token) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(orderService.getAllOrdersWithFilters(dto, pageable, token));
    }
}
