package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {
    private String recipient;
    private String subject;
    private String message;
    private String resetLink;
}
