package com.mercuryCyclists.Sales.service;

import com.google.gson.JsonObject;
import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.repository.InStoreSaleRepository;
import com.mercuryCyclists.Sales.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InStoreSaleService {

    private final InStoreSaleRepository inStoreSaleRepository;
    private final StoreRepository storeRepository;
    private final SaleService saleService;

    @Autowired
    public InStoreSaleService(InStoreSaleRepository inStoreSaleRepository, StoreRepository storeRepository, SaleService saleService) {
        this.inStoreSaleRepository = inStoreSaleRepository;
        this.storeRepository = storeRepository;
        this.saleService = saleService;
    }

    /**
     * Gets all In Store Sales
     */
    public List<InStoreSale> getAllInStoreSales() {
        return inStoreSaleRepository.findAll();
    }

    /**
     * Gets In Store Sales by Store ID
     */
    public List<InStoreSale> getStoreSales(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);

        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        return inStoreSaleRepository.findInStoreSalesByStore(store.get());
    }

    /**
     * Register a new Store
     */
    public InStoreSale registerInStoreSale(InStoreSale inStoreSale) {
       if (!inStoreSale.validate()) {
           throw new IllegalStateException("Invalid store");
       }

       inStoreSaleRepository.save(inStoreSale);
       return inStoreSale;
    }

    /**
     * Add store to in store sale
     */
    public InStoreSale addStoreToInStoreSale(Long inStoreSaleId, Long storeId) {
        Optional<InStoreSale> inStoreSale = inStoreSaleRepository.findById(inStoreSaleId);
        if (!inStoreSale.isPresent()) {
            throw new IllegalStateException(String.format("In Store Sale with Id %s does not exist", inStoreSaleId));
        }

        Optional<Store> store = storeRepository.findById(storeId);
        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        InStoreSale inStoreSaleObj = inStoreSale.get();
        Store storeObj = store.get();

        inStoreSaleObj.setStore(storeObj);
        inStoreSaleRepository.save(inStoreSaleObj);
        return inStoreSaleObj;
    }

    /**
     * Updates existing in store sale based on the in store sale given
     */
    public InStoreSale updateInStoreSale(InStoreSale inStoreSale, Long inStoreSaleId) {
        if (!inStoreSale.validate()) {
            throw new IllegalStateException("Invalid in store sale");
        }

        JsonObject product = saleService.getSaleProduct(inStoreSale);
        if(product.get("id") == null){
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }

        Optional<InStoreSale> existingInSoreSale = inStoreSaleRepository.findById(inStoreSaleId);
        if (!existingInSoreSale.isPresent()) {
            throw new IllegalStateException(String.format("in store sale with Id %s does not exist", inStoreSaleId));
        }

        inStoreSaleRepository.save(inStoreSale);
        return inStoreSale;
    }

    /**
     * Deletes existing in store sale based on ID
     */
    public void deleteInStoreSale(Long inStoreSaleId) {
        Optional<InStoreSale> existingInSoreSale = inStoreSaleRepository.findById(inStoreSaleId);
        if (!existingInSoreSale.isPresent()) {
            throw new IllegalStateException(String.format("in store sale with Id %s does not exist", inStoreSaleId));
        }

        inStoreSaleRepository.delete(existingInSoreSale.get());
    }
}
