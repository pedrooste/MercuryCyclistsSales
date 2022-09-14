package com.mercuryCyclists.Sales.repository;


import com.mercuryCyclists.Sales.entity.OnlineSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for OnlineSale
 */
@Repository
public interface OnlineSaleRepository extends JpaRepository<OnlineSale, Long> {
}
