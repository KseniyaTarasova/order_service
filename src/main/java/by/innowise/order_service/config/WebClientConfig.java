package by.innowise.order_service.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
public class WebClientConfig {
    @Value("${user-service.host}")
    private String userServiceHost;

    @Value("${user-service.port}")
    private String userServicePort;

    private static final int TIMEOUT = 5000;

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(String.format("http://%s:%s/api/v1/", userServiceHost, userServicePort))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(TIMEOUT))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                ))
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        request -> {
                            log.info("Request: {} {}", request.method(), request.url());
                            return Mono.just(request);
                        }
                ))
                .build();
    }
}
