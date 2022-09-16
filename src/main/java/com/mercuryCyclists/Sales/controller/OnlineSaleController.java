package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.InStoreSale;
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
}
