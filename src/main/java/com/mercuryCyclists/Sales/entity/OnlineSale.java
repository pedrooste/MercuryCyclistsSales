package com.mercuryCyclists.Sales.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * OnlineSale Entity / DAO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "online_sale")
public class OnlineSale extends Sale {
    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "address")
    private String address;

    public boolean validate(){
        return super.validate() && customerName != null && address != null;
    }

    @Override
    public String toString(){
        return String.format("Id: %s, Name: %s, Address: %s, productId: %s, Quantity: %s, Date: %s",
                this.getId(), this.getCustomerName(), this.getAddress(), this.getProductId(), this.getQuantity(), this.getDateTime().toString());
    }

}

