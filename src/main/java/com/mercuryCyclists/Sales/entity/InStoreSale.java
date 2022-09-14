package com.mercuryCyclists.Sales.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * InStoreSale Entity / DAO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "in_store_sale")
public class InStoreSale extends Sale{
    @Column(name = "receipt_no", unique = true)
    private Integer receiptNo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    public boolean validate(){
        return receiptNo != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InStoreSale that = (InStoreSale) o;
        return Objects.equals(receiptNo, that.receiptNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiptNo);
    }
}