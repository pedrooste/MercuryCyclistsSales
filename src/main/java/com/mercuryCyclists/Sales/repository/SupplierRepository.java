package com.mercuryCyclists.Sales.repository;

import com.mercuryCyclists.Sales.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for supplier
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
