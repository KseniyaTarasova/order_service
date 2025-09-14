package by.innowise.order_service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Transient
    private BigDecimal totalCost;

    @PrePersist
    public void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    @PostLoad
    public void calculateTotalCost() {
        this.totalCost = this.orderItems.stream()
                .map(orderItem -> orderItem
                        .getItem()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
