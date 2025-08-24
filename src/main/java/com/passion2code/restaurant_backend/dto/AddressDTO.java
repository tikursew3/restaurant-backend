package com.passion2code.restaurant_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
