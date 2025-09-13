package by.innowise.order_service.dto.item;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record UpdateItemDto(
        @NotBlank(message = "Item name is required")
        @Length(min = 1, max = 50, message = "Item name must be between 1 and 50 characters")
        String name,

        @NotNull(message = "Item price is required")
        @DecimalMin(value = "0.01", message = "Item price must be at least 0.01")
        @DecimalMax(value = "1000000.00", message = "Item price must be at most 1000000.00")
        @Digits(integer = 8, fraction = 2, message = "Item price must have 2 decimal places")
        BigDecimal price,

        @Positive(message = "Quantity must be greater than 0")
        int quantity
) {
}
