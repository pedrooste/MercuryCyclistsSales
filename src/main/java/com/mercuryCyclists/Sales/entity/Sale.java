package com.mercuryCyclists.Sales.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

/**
 * Sale Entity / DAO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class Sale {
    @Id
    @GeneratedValue
    @Type(type="uuid-char")
    @Column(name = "sale_id")
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "date_time")
    private Date dateTime = new Date(System.currentTimeMillis());

    protected boolean validate(){
        return dateTime != null && quantity >= 0;
    }
}