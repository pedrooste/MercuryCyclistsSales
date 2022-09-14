package com.mercuryCyclists.Sales.controller;


import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.Store;
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

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * Gets all Stores
     */
    @GetMapping
    public List<Store> getStores() {
        return storeService.getAllStores();
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
    @PutMapping(path = "/{storeId}")
    public Store updateStore(@RequestBody Store store, @PathVariable("storeId") Long storeId) {
        return storeService.updateStore(store, storeId);
    }

    /**
     * Deletes existing store based on ID
     */
    @DeleteMapping(path = "{storeId}")
    public void deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.deleteStore(storeId);
    }
}
