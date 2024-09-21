package org.e2e.labe2e02.passenger.dto;

import lombok.Data;

@Data
public class PassengerResponseDto{
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Float avgRating;
}