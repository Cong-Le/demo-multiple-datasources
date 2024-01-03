package com.example.demo_multiple_datasources.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateUserRequest {
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
