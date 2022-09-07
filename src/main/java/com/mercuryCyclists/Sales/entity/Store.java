package com.mercuryCyclists.Sales.entity;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Store Entity / DAO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "manager")
    private String manager;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Set<InStoreSale> inStoreSales = new LinkedHashSet<>();

    public boolean validate(){
        if (address == null || manager == null){
            return false;
        }
        return true;
    }
}