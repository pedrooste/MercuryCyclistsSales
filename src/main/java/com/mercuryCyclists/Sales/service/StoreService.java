package com.mercuryCyclists.Sales.service;

import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.repository.InStoreSaleRepository;
import com.mercuryCyclists.Sales.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for store
 */
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final InStoreSaleRepository inStoreSaleRepository;

    @Autowired
    StoreService(StoreRepository storeRepository, InStoreSaleRepository inStoreSaleRepository) {
        this.storeRepository = storeRepository;
        this.inStoreSaleRepository = inStoreSaleRepository;
    }

    /**
     * Gets all Stores
     */
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    /**
     * Register a new Store
     */
    public Store registerStore(Store store) {
        if (!store.validate()) {
            throw new IllegalStateException("Invalid store");
        }

        storeRepository.save(store);
        return store;
    }

    /**
     * Updates existing store based on the store given
     */
    public Store updateStore(Store store, Long storeId) {
        if (!store.validate()) {
            throw new IllegalStateException("Invalid store");
        }

        Optional<Store> existingStore = storeRepository.findById(storeId);
        if (!existingStore.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        storeRepository.save(store);
        return store;
    }

    /**
     * Deletes existing store based on ID
     */
    public void deleteStore(Long storeId) {
        Optional<Store> existingStore = storeRepository.findById(storeId);
        if (!existingStore.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        storeRepository.delete(existingStore.get());
    }
}

