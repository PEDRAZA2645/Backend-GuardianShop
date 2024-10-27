package com.americandevops.middleware.service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ErrorControlDto {

    @NotNull(message = "The field id cannot be null", groups = Update.class)
    @Null(message = "The field id must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The field errorId cannot be null", groups = {Create.class, Update.class})
    private Long errorId;

    @NotNull(message = "The field errorName cannot be null", groups = {Create.class, Update.class})
    private String errorName;

    @NotNull(message = "The field createdBy cannot be null", groups = {Create.class})
    @Null(message = "The field createdBy must be null", groups = Update.class)
    private String createdBy;

    @NotNull(message = "The field updatedBy cannot be null", groups = {Update.class})
    @Null(message = "The field updatedBy must be null", groups = Create.class)
    private String updatedBy;

    public interface Create {};
    public interface Update {};
}

