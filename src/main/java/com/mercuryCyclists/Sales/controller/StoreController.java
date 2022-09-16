package com.mercuryCyclists.Sales.controller;


import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.service.InStoreSaleService;
import com.mercuryCyclists.Sales.service.OnlineSaleService;
import com.mercuryCyclists.Sales.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for store
 */

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/store")
public class StoreController {

    private final StoreService storeService;
    private InStoreSaleService inStoreSaleService;

    @Autowired
    public StoreController(StoreService storeService, InStoreSaleService inStoreSaleService, OnlineSaleService onlineSaleService) {
        this.storeService = storeService;
        this.inStoreSaleService = inStoreSaleService;

    }

    /**
     * Gets all Stores
     */
    @GetMapping
    public List<Store> getStores() {
        return storeService.getAllStores();
    }

    /**
     * Gets In Store Sales by Store ID
     */
    // TODO Test properly
    @GetMapping(path = "/{storeId}/sales")
    public List<InStoreSale> getStoreSales(@PathVariable("storeId") Long storeId) {
        return storeService.getStoreSales(storeId);
    }

    /**
     * Register a new Store
     */
    @PostMapping()
    public Store registerStore(@RequestBody Store store) {
        return storeService.registerStore(store);
    }

    /**
     * Updates existing store based on the store given
     */
    @PutMapping()
    public Store updateStore(@RequestBody Store store) {
        return storeService.updateStore(store);
    }

    /**
     * Deletes existing store based on ID
     */
    @DeleteMapping(path = "{storeId}")
    public void deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.deleteStore(storeId);
    }
}
