package com.passion2code.restaurant_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // You can add apartment/unit later if needed
    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    // Keep short codes like "TX", "MN" etc., or expand later
    @Column(nullable = false, length = 64)
    private String state;

    @Column(nullable = false, length = 32)
    private String postalCode;

    @Column(nullable = false, length = 64)
    private String country;
}
