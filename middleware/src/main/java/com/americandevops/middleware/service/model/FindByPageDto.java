package com.americandevops.middleware.service.model;

import lombok.Getter;
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
