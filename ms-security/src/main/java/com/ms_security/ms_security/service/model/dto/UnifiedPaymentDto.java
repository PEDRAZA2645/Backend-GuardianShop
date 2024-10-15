package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnifiedPaymentDto {
    private Float amount;
    private String paymentMethod;
    private String currency = "USD";
    private String description = "Payment for Order";
}
