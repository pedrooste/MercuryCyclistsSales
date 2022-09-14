package com.mercuryCyclists.Sales.repository;

import com.mercuryCyclists.Sales.entity.InStoreSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for InStoreSale
 */
@Repository
public interface InStoreSaleRepository extends JpaRepository<InStoreSale, Long> {
}
