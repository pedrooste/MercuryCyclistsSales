package com.mercuryCyclists.Sales.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

/**
 * Sale Entity / DAO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public abstract class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "date_time")
    private Date dateTime;

    protected boolean validate(){
        return quantity != null && dateTime != null;
    }
}