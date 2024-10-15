package com.ms_security.ms_security.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametersDTO {

    private Long id;
    @NotNull(groups = ParametersDTOFindByCode.class)
    private Long codeParameter;
    private String descriptionParameter;
    private String parameter;
    private String userUpdate;

    public interface ParametersDTOFindByCode {}
}
