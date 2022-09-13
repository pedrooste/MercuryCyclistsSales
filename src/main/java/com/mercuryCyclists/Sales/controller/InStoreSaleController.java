package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.service.InStoreSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for in store sale
 */

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
    @PostMapping(path = "/store/{storeId}")
    public ResponseEntity<String> registerInStoreSale(@RequestBody InStoreSale inStoreSale,
                                                           @PathVariable("storeId") Long storeId) {
        return inStoreSaleService.registerInStoreSale(inStoreSale, storeId);
    }

    /**
     * Get product by sale
     * @param id
     * @return
     */
    @GetMapping(path = "{id}/product")
    public ResponseEntity<String> getProductBySale(@PathVariable("id") Long id)  {
        //check that the sale exists
        InStoreSale s = inStoreSaleService.getInstoreSale(id);
        //if the sale exists get the product associated with the sale by ID
        return inStoreSaleService.getProductBySaleId(s);
    }

    /**
     * Create backorder for in store sale
     */
    @PostMapping(path = "/backorder/store/{storeId}")
    public InStoreSale createBackorder(@RequestBody InStoreSale inStoreSale, @PathVariable("storeId") Long storeId) {
        return inStoreSaleService.registerBackorder(inStoreSale, storeId);
    }
}
