package com.example.store.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String description;
    private OrderCustomerDTO customer;
    
    @NotEmpty(message = "Order must contain at least one product")
    private List<Long> productIds;
}
