package com.passion2code.restaurant_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_locations")
@NoArgsConstructor @AllArgsConstructor @Builder
@Data
public class StoreLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;      // e.g., "Main Street"
    private String phone;                               // store contact

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    // Optional: JSON/text of hours like "Mon–Fri 9–9, Sat–Sun 10–8"
    @Column(columnDefinition = "TEXT")
    private String openingHours;
}
