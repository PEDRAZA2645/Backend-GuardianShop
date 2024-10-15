package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderPdfDto {

    private Long orderNumber;
    private String name;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
