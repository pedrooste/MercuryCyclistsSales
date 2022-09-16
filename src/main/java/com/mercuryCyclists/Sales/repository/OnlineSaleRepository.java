package com.mercuryCyclists.Sales.repository;


import com.mercuryCyclists.Sales.entity.OnlineSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OnlineSale
 */
@Repository
public interface OnlineSaleRepository extends JpaRepository<OnlineSale, Long> {
    List<OnlineSale> findByProductId(Long productId);
}
