package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.service.InStoreSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/in-store-sale")
public class InStoreSaleController {

    private final InStoreSaleService inStoreSaleService;

    @Autowired
    public InStoreSaleController(InStoreSaleService inStoreSaleService) {
        this.inStoreSaleService = inStoreSaleService;
    }

    /**
     * Gets all In Store Sales
     */
    @GetMapping
    public List<InStoreSale> getInStoreSales() {
        return inStoreSaleService.getAllInStoreSales();
    }

    /**
     * Gets In Store Sales by Store ID
     */
    @GetMapping(path = "/store/{storeId}/sales")
    public List<InStoreSale> getStoreSales(@PathVariable("storeId") Long storeId) {
        return inStoreSaleService.getStoreSales(storeId);
    }

    /**
     * Register a new In Store Sale
     */
    @PostMapping
    public InStoreSale registerInStoreSale(@RequestBody InStoreSale inStoreSale) {
        return inStoreSaleService.registerInStoreSale(inStoreSale);
    }

    /**
     * Add store to in store sale
     */
    @PostMapping(path = "/{inStoreSaleId}/store/{storeId}/add-store")
    public InStoreSale addStoreToInStoreSale(@PathVariable("inStoreSaleId") Long inStoreSaleId,
                                             @PathVariable("storeId") Long storeId) {
        return inStoreSaleService.addStoreToInStoreSale(inStoreSaleId, storeId);
    }

    /**
     * Updates existing in store sale based on the in store sale given
     */
    @PutMapping(path = "/{inStoreSaleId}")
    public InStoreSale updateInStoreSale(@RequestBody InStoreSale inStoreSale,
                                         @PathVariable("inStoreSaleId") Long inStoreSaleId) {
        return inStoreSaleService.updateInStoreSale(inStoreSale, inStoreSaleId);
    }

    /**
     * Deletes existing in store sale based on ID
     */
    @DeleteMapping(path = "/{inStoreSaleId}")
    public void deleteInStoreSale(@PathVariable("inStoreSaleId") Long inStoreSaleId) {
        inStoreSaleService.deleteInStoreSale(inStoreSaleId);
    }
}
