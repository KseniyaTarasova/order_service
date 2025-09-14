package by.innowise.order_service;

import by.innowise.order_service.dto.OrderItemDto;
import by.innowise.order_service.dto.UserDto;
import by.innowise.order_service.dto.order.CreateOrderDto;
import by.innowise.order_service.dto.order.OrderDto;
import by.innowise.order_service.entity.Item;
import by.innowise.order_service.entity.Order;
import by.innowise.order_service.entity.OrderStatus;
import by.innowise.order_service.repository.ItemRepository;
import by.innowise.order_service.repository.OrderRepository;
import by.innowise.order_service.service.ItemService;
import by.innowise.order_service.service.UserService;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT {

    private static final String USER_EMAIL = "/users/email/.*";
    private static final String USER_IDS = "/users/ids";
    private static final String BASE_URL = "/orders/";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");


    @Container
    static WireMock wireMockContainer = new WireMockContainer("wiremock/wiremock:3.8.0")
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("user-service.url",
                () -> "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(8080));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private static WireMock wireMock;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final CreateOrderDto orderRequest = new CreateOrderDto();
    private Order testOrder;

    private final Long userId = 1L;

    private final String userServiceResponse = """
            {
              "id":"%s",
              "name": "Test",
              "surname": "Test",
              "birthDate": "2020-10-10",
              "email": "test@gmail.com"
            }
            """.formatted(userId);

    @BeforeAll
    static void setUpWireMock() {
        wireMock = new WireMock(wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));
    }

    @BeforeEach
    void initialize() {
        Item item = new Item();
        item.setName("test");
        item.setPrice(BigDecimal.valueOf(10.00));
        Item testItem = itemRepository.save(item);

        orderItemRequest.setQuantity(10L);
        orderItemRequest.setItemId(testItem.getId());

        orderRequest.setStatus(OrderStatus.CREATED);
        orderRequest.setUserEmail("test@gmail.com");
        orderRequest.setOrderItems(List.of(orderItemRequest));

        wireMock.resetAll();
    }

    private void initUserResponse() {
        wireMock.register(get(urlPathMatching(USER_EMAIL))
                .willReturn(aResponse()
                        .withBody(userServiceResponse)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.OK.value())));
    }

    private void initUserResponseNotFound() {
        wireMock.register(get(urlPathMatching(USER_EMAIL))
                .willReturn(aResponse().withStatus(404)));
    }

    private void initUserResponses() {
        wireMock.register(post(urlPathMatching(USER_IDS))
                .willReturn(aResponse()
                        .withBody("[" + userServiceResponse + "]")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.OK.value())));
    }

    private void saveOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setUserId(UUID.fromString(userId));
        order.setOrderItems(new ArrayList<>());
        order.setCreationDate(LocalDate.now());
        testOrder = orderRepository.save(order);
    }

    private void saveOrderMismatch() {
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);
        order.setUserId(1L);
        order.setOrderItems(new ArrayList<>());
        order.setCreationDate(LocalDateTime.now());
        testOrder = orderRepository.save(order);
    }

    private HttpEntity<CreateOrderDto> initRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token");
        return new HttpEntity<>(orderRequest, headers);
    }

    private HttpEntity<Void> initEmptyRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token");
        return new HttpEntity<>(headers);
    }

    @Test
    void testCreateOrder() {
        initUserResponse();
        HttpEntity<CreateOrderDto> requestEntity = initRequest();

        ResponseEntity<OrderDto> response = restTemplate.postForEntity(
                BASE_URL, requestEntity, OrderDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUserInfo().getEmail())
                .isEqualTo(orderRequest.getUserEmail());
    }

    @Test
    void testCreateOrderItemNotFound() {
        initUserResponse();
        orderItemRequest.setItemId(1L);

        HttpEntity<OrderRequest> requestEntity = initRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                BASE_URL, requestEntity, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateOrderUserNotFound() {
        initUserResponseNotFound();
        HttpEntity<OrderRequest> requestEntity = initRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/v1/orders", requestEntity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindOrderById() {
        saveOrder();
        initUserResponse();
        HttpEntity<Void> entity = initEmptyRequest();
        ResponseEntity<OrderResponse> response = restTemplate.exchange(BASE_URL + testOrder.getId() + "/test@gmail.com", HttpMethod.GET, entity, OrderResponse.class);
        assertThat(response.getBody().getId()).isEqualTo(testOrder.getId());
    }

    @Test
    void testFindOrderByIdNotFound() {
        HttpEntity<Void> entity = initEmptyRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + UUID.randomUUID() + "/test@gmail.com", HttpMethod.GET, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindOrderByIdUserNotFound() {
        saveOrder();
        initUserResponseNotFound();
        HttpEntity<Void> entity = initEmptyRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + testOrder.getId() + "/test@gmail.com", HttpMethod.GET, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindOrderByIdMismatch() {
        saveOrderMismatch();
        initUserResponse();
        HttpEntity<Void> entity = initEmptyRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + testOrder.getId() + "/test@gmail.com", HttpMethod.GET, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testFindByIds() {
        saveOrder();
        initUserResponses();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token");
        HttpEntity<List<UUID>> entity = new HttpEntity<>(List.of(testOrder.getId()), headers);
        ResponseEntity<List<OrderResponse>> response = restTemplate.exchange("/api/v1/orders/ids", HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });
        assertThat(response.getBody().getFirst().getId()).isEqualTo(testOrder.getId());
    }

    @Test
    void testFindByStatuses() {
        orderRepository.deleteAll();
        saveOrder();
        initUserResponses();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token");
        HttpEntity<List<OrderStatus>> entity = new HttpEntity<>(List.of(OrderStatus.CREATED), headers);
        ResponseEntity<List<OrderResponse>> response = restTemplate.exchange(BASE_URL + "statuses", HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(testOrder.getId());
    }

    @Test
    void testUpdateOrder() {
        saveOrder();
        initUserResponses();
        OrderRequest request = new OrderRequest();
        request.setStatus(OrderStatus.COMPLETED);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("token");
        HttpEntity<OrderRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<OrderResponse> response = restTemplate.exchange(BASE_URL + testOrder.getId() + "/test@gmail.com", HttpMethod.PUT, entity, OrderResponse.class);
        assertThat(response.getBody().getStatus()).isEqualTo(request.getStatus().toString());
    }

    @Test
    void testUpdateOrderNotFound() {
        HttpEntity<OrderRequest> entity = initRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + UUID.randomUUID() + "/test@gmail.com", HttpMethod.PUT, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateOrderUserNotFound() {
        saveOrder();
        initUserResponseNotFound();
        HttpEntity<OrderRequest> entity = initRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + UUID.randomUUID() + "/test@gmail.com", HttpMethod.PUT, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateOrderMismatch() {
        saveOrderMismatch();
        initUserResponse();
        HttpEntity<OrderRequest> entity = initRequest();
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + testOrder.getId() + "/test@gmail.com", HttpMethod.PUT, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteOrderById() {
        saveOrder();
        HttpEntity<Void> entity = initEmptyRequest();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_URL + testOrder.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(BASE_URL + testOrder.getId() + "/test@gmail.com", HttpMethod.GET, entity, ErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(BASE_URL + UUID.randomUUID(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
