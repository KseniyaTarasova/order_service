package by.innowise.order_service.util;

import by.innowise.order_service.dto.item.ItemFilterDto;
import by.innowise.order_service.entity.Item;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {
    public static Specification<Item> withFilter(ItemFilterDto filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.name() != null && !filter.name().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + filter.name().toLowerCase() + "%"));
            }

            if (filter.minPrice() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
            }

            if (filter.maxPrice() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
            }

            if (filter.minQuantity() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("quantity"), filter.minQuantity()));
            }

            if (filter.maxQuantity() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("quantity"), filter.maxQuantity()));
            }

            return predicate;
        };
    }
}
