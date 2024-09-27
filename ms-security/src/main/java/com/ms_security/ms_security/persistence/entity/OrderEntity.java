package com.ms_security.ms_security.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "ORDERS", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID", nullable = false)
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "TOTAL_COST", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "STATUS", nullable = false)
    private String status; // e.g., "PENDING", "PAID", "CANCELLED"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
    private InventoryEntity product;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "DATETIME_CREATION")
    private String dateTimeCreation;

    @Column(name = "DATETIME_UPDATE")
    private String dateTimeUpdate;

    @Column(name = "DATETIME_ORDER")
    private String dateTimeOrder;

}
