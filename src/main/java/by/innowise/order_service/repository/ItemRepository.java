package by.innowise.order_service.repository;

import by.innowise.order_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>,
        JpaSpecificationExecutor<Item> {
}
