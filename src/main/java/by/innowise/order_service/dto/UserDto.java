package by.innowise.order_service.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email
) {
}
