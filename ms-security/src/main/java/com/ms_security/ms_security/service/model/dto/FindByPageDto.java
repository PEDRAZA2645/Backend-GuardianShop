package com.ms_security.ms_security.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FindByPageDto implements Serializable {
    private Long page;
    private Long size;
    private String sortBy;
    private String direction;
}
