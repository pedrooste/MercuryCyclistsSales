package com.mercuryCyclists.Sales.repository;

import com.mercuryCyclists.Sales.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Store
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
