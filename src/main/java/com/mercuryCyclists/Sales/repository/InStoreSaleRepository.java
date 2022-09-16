package com.mercuryCyclists.Sales.repository;

import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InStoreSale
 */
@Repository
public interface InStoreSaleRepository extends JpaRepository<InStoreSale, Long> {
    List<InStoreSale> findInStoreSalesByStore(Store store);
}
