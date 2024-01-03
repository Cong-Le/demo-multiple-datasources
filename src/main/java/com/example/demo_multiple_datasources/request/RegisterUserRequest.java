package com.example.demo_multiple_datasources.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
