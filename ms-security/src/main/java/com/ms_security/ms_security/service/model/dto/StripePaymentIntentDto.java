package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripePaymentIntentDto {
    public enum Currency{
        USD, EUR;
    }
    private String description;
    private int amount;
    private Currency currency;
}
