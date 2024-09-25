package com.ms_security.ms_security.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "SERVICES", schema = "SECURITY_MICROSERVICES")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ServicesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERVICES_ID", nullable = false)
    private Long id;
    @Column(name = "NAME_SERVICE", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @Column(name = "IMAGE_URL", nullable = false)
    private String imageUrl;
    @Column(name = "STATUS", nullable = false)
    private Boolean status;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;
    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;
}
