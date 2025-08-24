package com.passion2code.restaurant_backend.dto;

import com.passion2code.restaurant_backend.enums.FulfillmentMethod;
import com.passion2code.restaurant_backend.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckoutRequest {
    private String cartToken;

    private String customerFirstName;
    private String customerLastName;
    private String customerPhone;          // required for both flows

    private FulfillmentMethod fulfillmentMethod; // PICKUP or DELIVERY

    // DELIVERY only (required when fulfillmentMethod == DELIVERY)
    private AddressDTO deliveryAddress;

    // Optional: billing address (useful for CARD payments)
    private AddressDTO billingAddress;

    // PICKUP: optional if you have multiple stores
    private Long storeLocationId;

    // CARD or CASH (we’ll mark Payment as PENDING here)
    private PaymentMethod paymentMethod;
}
