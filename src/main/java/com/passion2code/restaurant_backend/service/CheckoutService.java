package com.passion2code.restaurant_backend.service;

import com.passion2code.restaurant_backend.dto.AddressDTO;
import com.passion2code.restaurant_backend.dto.CheckoutRequest;
import com.passion2code.restaurant_backend.dto.CheckoutResponse;
import com.passion2code.restaurant_backend.enums.*;
import com.passion2code.restaurant_backend.model.*;
import com.passion2code.restaurant_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final StoreLocationRepository storeLocationRepository; // <-- now injected

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest req) {
        // 1) Basic validations
        if (req.getCartToken() == null || req.getCartToken().isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }
        if (req.getCustomerFirstName() == null || req.getCustomerLastName() == null) {
            throw new IllegalArgumentException("Customer first/last name are required");
        }
        if (req.getCustomerPhone() == null || req.getCustomerPhone().isBlank()) {
            throw new IllegalArgumentException("Customer phone is required");
        }

        FulfillmentMethod method = req.getFulfillmentMethod() == null
                ? FulfillmentMethod.PICKUP
                : req.getFulfillmentMethod();

        // 2) Load cart
        Cart cart = cartRepository.findByCartTokenAndStatus(req.getCartToken(), CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found for token"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // 3) Create Order (without items yet)
        Order order = Order.builder()
                .customerFirstName(req.getCustomerFirstName())
                .customerLastName(req.getCustomerLastName())
                .customerPhone(req.getCustomerPhone())
                .fulfillmentMethod(method) // default is PICKUP, set explicitly anyway
                .orderStatus(OrderStatus.PLACED)
                .build();

        // DELIVERY requires a delivery address
        if (method == FulfillmentMethod.DELIVERY) {
            if (req.getDeliveryAddress() == null) {
                throw new IllegalArgumentException("Delivery address is required for DELIVERY");
            }
            Address deliveryAddress = toAddress(req.getDeliveryAddress());
            addressRepository.save(deliveryAddress);
            order.setDeliveryAddress(deliveryAddress);
        }

        // PICKUP: attach a store location if provided/needed
        if (method == FulfillmentMethod.PICKUP && req.getStoreLocationId() != null) {
            StoreLocation store = storeLocationRepository.findById(req.getStoreLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Store location not found"));
            order.setStoreLocation(store);
        }

        // 4) Convert cart lines -> order items and compute total (NO lambda mutation)
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cart.getItems()) {
            if (Boolean.FALSE.equals(ci.getMenuItem().getIsActive())) {
                throw new IllegalStateException("Item '" + ci.getMenuItem().getName() + "' is inactive");
            }
            if (ci.getQuantity() == null || ci.getQuantity() <= 0) {
                throw new IllegalStateException("Invalid quantity for item '" + ci.getMenuItem().getName() + "'");
            }

            BigDecimal lineTotal = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(lineTotal);

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .menuItem(ci.getMenuItem())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice()) // snapshot from cart
                    .build();
            orderItems.add(oi);
        }

        order.setOrderItems(orderItems);
        order = orderRepository.save(order); // cascades items

        // 5) Optional billing address (esp. for CARD)
        Address billingAddress = null;
        if (req.getBillingAddress() != null) {
            billingAddress = toAddress(req.getBillingAddress());
            addressRepository.save(billingAddress);
        }

        // 6) Create Payment in PENDING
        Payment payment = Payment.builder()
                .order(order)
                .billingAddress(billingAddress)
                .amount(total)
                .method(req.getPaymentMethod() == null ? PaymentMethod.CASH : req.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        // 7) Mark cart as converted and clear lines (orphanRemoval will delete items)
        cart.setStatus(CartStatus.CONVERTED);
        cart.getItems().clear();
        cartRepository.save(cart);

        return new CheckoutResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getFulfillmentMethod(),
                total,
                payment.getPaymentStatus()
        );
    }

    private Address toAddress(AddressDTO dto) {
        if (dto.getStreet() == null || dto.getCity() == null ||
                dto.getState() == null || dto.getPostalCode() == null || dto.getCountry() == null) {
            throw new IllegalArgumentException("All address fields are required");
        }
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .build();
    }


}
