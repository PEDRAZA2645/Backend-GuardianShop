package com.americandevops.middleware.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "ERROR_CONTROL", schema = "MIDDLEWARE")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ErrorControlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ERROR_CONTROL_ID")
    private Long id;

    @Column(name = "ERROR_ID")
    private Long errorId;

    @Column(name = "ERROR_NAME")
    private String errorName;

    @Column(name = "CREATE_USER")
    private String createdBy;

    @Column(name = "UPDATE_USER")
    private String updatedBy;

    @Column(name = "DATE_TIME_CREATION")
    private Date dateTimeCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private Date dateTimeUpdate;

}
