package com.example.demo;

import com.example.demo.dto.CustomerDto;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Autowired
    private WebTestClient client;

    @Test
    @Order(1)
    public void allCustomers() {
        this.client.get()
                .uri("/customers")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(CustomerDto.class)
                .value(list -> log.info("{}", list))
                .hasSize(10);
    }

    @Test
    @Order(2)
    public void paginatedCustomers() {
        this.client.get()
                .uri("/customers/paginated?page=3&size=2")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .consumeWith(response -> log.info("{}", new String(response.getResponseBody())))
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content.[0].id").isEqualTo(7)
                .jsonPath("$.content.[1].id").isEqualTo(8);
    }

    @Test
    @Order(3)
    public void getCustomersById() {
        this.client.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .consumeWith(response -> log.info("{}", new String(requireNonNull(response.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("mark")
                .jsonPath("$.email").isEqualTo("sam@gmail.com");
    }

    @Test
    @Order(4)
    public void createCustomer() {
        var customerDto = new CustomerDto(null, "rafael", "rafael@gmail.com");
        this.client.post()
                .uri("/customers")
                .bodyValue(customerDto)
                //.body(Mono.just(customerDto), CustomerDto.class)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(resp -> log.info("{}", new String(requireNonNull(resp.getResponseBody()))))
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(customerDto.name())
                .jsonPath("$.email").isEqualTo(customerDto.email());

    }

    @Test
    @Order(5)
    public void updateCustomer() {
        var customerDto = new CustomerDto(null, "updated name", "updated@gmail.com");
        this.client.put()
                .uri("/customers/1")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(resp -> log.info("{}", new String(requireNonNull(resp.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo(customerDto.name())
                .jsonPath("$.email").isEqualTo(customerDto.email());
    }

    @Test
    @Order(6)
    public void deleteCustomer() {
        this.client.delete()
                .uri("/customers/11")
                .exchange()
                .expectStatus().is2xxSuccessful();

        this.client.get()
                .uri("/customers/11")
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(404));
    }
}
