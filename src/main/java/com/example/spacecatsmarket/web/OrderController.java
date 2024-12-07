package com.example.spacecatsmarket.web;


import com.example.spacecatsmarket.domain.order.OrderContext;
import com.example.spacecatsmarket.dto.order.PlaceOrderRequestDto;
import com.example.spacecatsmarket.dto.order.PlaceOrderResponseDto;
import com.example.spacecatsmarket.service.OrderService;
import com.example.spacecatsmarket.web.mapper.OrderDtoMapper;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/internal/{customerReference}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    @PostMapping("/{cartId}")
    public ResponseEntity<PlaceOrderResponseDto> placeOrder(
        @PathVariable("customerReference") UUID customerReference,
        @PathVariable("cartId") String cartId,
        @RequestBody @Valid PlaceOrderRequestDto placeOrderDto) {
        log.info("Placing the order for cart with id : {}", cartId);
        OrderContext context = orderDtoMapper.toOrderContext(cartId, customerReference, placeOrderDto);
        return ResponseEntity.ok(orderDtoMapper.toPlaceOrderResponseDto(orderService.placeOrder(context)));
    }
}

