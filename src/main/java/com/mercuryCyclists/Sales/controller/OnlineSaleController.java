package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.service.OnlineSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Register a new Online Sale
     */
    @PostMapping
    public ResponseEntity<OnlineSale> registerOnlineSale(@RequestBody OnlineSale onlineSale) {
        return onlineSaleService.registerOnlineSale(onlineSale);
    }
}
