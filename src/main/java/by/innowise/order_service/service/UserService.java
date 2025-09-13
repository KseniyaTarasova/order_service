package by.innowise.order_service.service;

import by.innowise.order_service.dto.UserDto;
import by.innowise.order_service.exception.AuthorizationException;
import by.innowise.order_service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient userServiceWebClient;

    public UserDto getUserByEmail(String token) {
        return userServiceWebClient.get()
                .uri("/users/email")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        e -> Mono.error(new UserNotFoundException("User not found")))
                .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                        e -> Mono.error(new AuthorizationException("Invalid token")))
                .onStatus(status -> status.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        e -> Mono.error(new RuntimeException("User Service unavailable")))
                .bodyToMono(UserDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(e -> e instanceof ServiceUnavailableException))
                .doOnSubscribe(sub -> log.debug("Fetching user by email"))
                .doOnSuccess(user -> log.debug("Successfully fetched user: {}", user.email()))
                .doOnError(error -> log.error("Failed to fetch user by email", error))
                .block();
    }
}
