package com.mercuryCyclists.Sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Column(name = "sale_id", nullable = false)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "date_time")
    private Date dateTime = new Date(System.currentTimeMillis());

    protected boolean validate(){
        return quantity != null && dateTime != null;
    }
}