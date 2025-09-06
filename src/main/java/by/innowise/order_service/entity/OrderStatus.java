package by.innowise.order_service.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PROCESSING("processing"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    REFUNDED("refunded");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
