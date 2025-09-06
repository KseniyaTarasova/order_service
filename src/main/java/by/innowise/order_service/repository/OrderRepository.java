package by.innowise.order_service.repository;

import by.innowise.order_service.entity.Order;
import by.innowise.order_service.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByStatusIn(List<OrderStatus> status);
}