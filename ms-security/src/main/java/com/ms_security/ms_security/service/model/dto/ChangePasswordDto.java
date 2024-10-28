package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {

    private String token;
    private String newPassword;
}
