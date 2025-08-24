package com.passion2code.restaurant_backend.model;

import com.passion2code.restaurant_backend.enums.FulfillmentMethod;
import com.passion2code.restaurant_backend.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor @AllArgsConstructor @Builder
@Data
@ToString(exclude = {"orderItems", "payment", "deliveryAddress", "storeLocation"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String customerFirstName;
    @Column(nullable = false) private String customerLastName;

    // Useful for both pickup and delivery
    @Column(nullable = false, length = 32)
    private String customerPhone;

    // DELIVERY only (nullable for PICKUP)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private Address deliveryAddress;

    // PICKUP only (nullable for DELIVERY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_location_id")
    private StoreLocation storeLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FulfillmentMethod fulfillmentMethod; // DELIVERY or PICKUP

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Optional timestamps for pickup tracking
    private LocalDateTime readyForPickupAt;
    private LocalDateTime pickedUpAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (orderStatus == null) orderStatus = OrderStatus.PLACED;
        if (fulfillmentMethod == null) fulfillmentMethod = FulfillmentMethod.PICKUP; // <-- default
    }

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Payment payment;
}
