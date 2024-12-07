package com.example.spacecatsmarket.web;

import com.example.spacecatsmarket.AbstractIt;
import com.example.spacecatsmarket.common.PaymentStatus;
import com.example.spacecatsmarket.common.ProductType;
import com.example.spacecatsmarket.dto.order.OrderEntryDto;
import com.example.spacecatsmarket.dto.order.PlaceOrderRequestDto;
import com.example.spacecatsmarket.dto.payment.PaymentClientResponseDto;
import com.example.spacecatsmarket.featuretoggle.FeatureToggleExtension;
import com.example.spacecatsmarket.service.OrderService;
import com.example.spacecatsmarket.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.spacecatsmarket.service.exception.PaymentTransactionFailed.PAYMENT_TRANSACTION_WITH_ID_S_FOR_CART_ID_S_FAILED;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.mockito.Mockito.reset;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("Order Controller IT")
@Tag("order-service")
class OrderControllerIT extends AbstractIt {

    private static final String CART_ID_1234 = "cartId1234";
    private final PlaceOrderRequestDto PLACE_ORDER_REQUEST_DTO = builPlaceOrderRequestDto();
    private final String CUSTOMER_REFERENCE = UUID.randomUUID().toString();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private PaymentService paymentService;

    @SpyBean
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        reset(paymentService, orderService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void shouldPlaceOrder() {
        UUID transaction = UUID.randomUUID();

        stubFor(WireMock.post("/payment-service/v1/payments")
            .willReturn(aResponse().withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsBytes(PaymentClientResponseDto.builder()
                    .uuid(transaction)
                    .consumerReference(CUSTOMER_REFERENCE)
                    .status(PaymentStatus.SUCCESS)
                    .build()))));

        mockMvc.perform(post("/api/v1/internal/{customerReference}/orders/{cartId}", CUSTOMER_REFERENCE, "cartId1234").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(PLACE_ORDER_REQUEST_DTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").exists())
            .andExpect(jsonPath("$.transactionId").value(transaction.toString()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123e4567-e89b-12d3-a456-426614174000", "223e4567-e89b-12d3-a456-426614174001"})
    @SneakyThrows
    @WithMockUser
    void shouldPlaceOrder(UUID transaction) {

        stubFor(WireMock.post("/payment-service/v1/payments")
            .willReturn(aResponse().withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsBytes(PaymentClientResponseDto.builder()
                    .uuid(transaction)
                    .consumerReference(CUSTOMER_REFERENCE)
                    .status(PaymentStatus.SUCCESS)
                    .build()))));

        mockMvc.perform(post("/api/v1/internal/{customerReference}/orders/{cartId}", CUSTOMER_REFERENCE, "cartId1234").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(PLACE_ORDER_REQUEST_DTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").exists())
            .andExpect(jsonPath("$.transactionId").value(transaction.toString()));
    }


    @Test
    @WithMockUser
    @SneakyThrows
    void shouldThrowPaymentTransactionFailedException() {
        UUID transaction = UUID.randomUUID();
        ProblemDetail problemDetail =
            ProblemDetail.forStatusAndDetail(BAD_REQUEST, String.format(PAYMENT_TRANSACTION_WITH_ID_S_FOR_CART_ID_S_FAILED, transaction, CART_ID_1234));

        problemDetail.setType(URI.create("payment-refused"));
        problemDetail.setTitle("Payment Transaction Failed to process");
        stubFor(WireMock.post("/payment-service/v1/payments")
            .willReturn(aResponse().withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsBytes(PaymentClientResponseDto.builder()
                    .uuid(transaction)
                    .consumerReference(CUSTOMER_REFERENCE)
                    .status(PaymentStatus.FAILURE)
                    .build()))));

        mockMvc.perform(post("/api/v1/internal/{customerReference}/orders/{cartId}", CUSTOMER_REFERENCE, CART_ID_1234).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(PLACE_ORDER_REQUEST_DTO)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(problemDetail)));

    }

    private static PlaceOrderRequestDto builPlaceOrderRequestDto() {
        return PlaceOrderRequestDto.builder().totalPrice(10.0).entries(List.of(builOrderEntryDto())).build();
    }

    private static OrderEntryDto builOrderEntryDto() {
        return OrderEntryDto.builder().productType(ProductType.COSMIC_CATNIP.getDisplayName()).quantity(1).build();
    }

}
