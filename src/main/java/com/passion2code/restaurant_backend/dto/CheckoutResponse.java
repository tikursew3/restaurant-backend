package com.passion2code.restaurant_backend.dto;

import com.passion2code.restaurant_backend.enums.FulfillmentMethod;
import com.passion2code.restaurant_backend.enums.OrderStatus;
import com.passion2code.restaurant_backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CheckoutResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private FulfillmentMethod fulfillmentMethod;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
}
