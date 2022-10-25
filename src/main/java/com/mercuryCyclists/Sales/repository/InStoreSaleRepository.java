package com.mercuryCyclists.Sales.repository;

import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for InStoreSale
 */
@Repository
public interface InStoreSaleRepository extends JpaRepository<InStoreSale, UUID> {
    List<InStoreSale> findInStoreSalesByStore(Store store);
}
