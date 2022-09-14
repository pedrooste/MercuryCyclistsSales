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
    private Long saleId;

    @Column(name = "product_id", unique = true)
    private Long productId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "date_time")
    @JsonFormat(pattern="dd/MM/yy")
    private Date dateTime;

    protected boolean validate(){
        return quantity != null && dateTime != null;
    }
}