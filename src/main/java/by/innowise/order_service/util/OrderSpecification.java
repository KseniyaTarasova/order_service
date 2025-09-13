package by.innowise.order_service.util;

import by.innowise.order_service.dto.OrderFilterDto;
import by.innowise.order_service.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> withFilter(OrderFilterDto filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.userId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("userId"), filter.userId()));
            }

            if (filter.statuses() != null && !filter.statuses().isEmpty()) {
                predicate = cb.and(predicate, root.get("status").in(filter.statuses()));
            }

            if (filter.ordersIds() != null && !filter.ordersIds().isEmpty()) {
                predicate = cb.and(predicate, root.get("id").in(filter.ordersIds()));
            }

            return predicate;
        };
    }
}
