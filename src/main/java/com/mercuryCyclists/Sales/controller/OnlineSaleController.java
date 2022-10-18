package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.service.OnlineSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for online sale
 */

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/online-sale")
public class OnlineSaleController {

    private final OnlineSaleService onlineSaleService;

    @Autowired
    public OnlineSaleController(OnlineSaleService onlineSaleService) {
        this.onlineSaleService = onlineSaleService;
    }

    /**
     * Create backorder for online sale
     */
    @PostMapping(path = "/backorder")
    public OnlineSale createBackorder(@RequestBody OnlineSale onlineSale) {
        return onlineSaleService.registerBackorder(onlineSale);
    }
    

    /**
     * Gets all Online Sales
     */
    @GetMapping
    public List<OnlineSale> GetOnlineSales() {
        return onlineSaleService.GetOnlineSales();
    }

    /**
     * Register a new Online Sale
     */
    @PostMapping
    public ResponseEntity<String> registerOnlineSale(@RequestBody OnlineSale onlineSale) {
        return onlineSaleService.registerOnlineSale(onlineSale);
    }

    /**
     * Get product by sale
     * @param id
     * @return
     */
    @GetMapping(path = "{id}/product")
    public ResponseEntity<String> getProductBySale(@PathVariable("id") Long id)  {
        //check that the sale exists
        OnlineSale s = onlineSaleService.getOnlineSale(id);
        //if the sale exists get the product associated with the sale by ID
        return onlineSaleService.getProductBySaleId(s);
    }
}
