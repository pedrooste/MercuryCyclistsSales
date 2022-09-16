package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.service.OnlineSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/online")
public class OnlineSaleController {
    private OnlineSaleService onlineSaleService;

    private static RestTemplate restTemplate = new RestTemplate();

    private static final String GETPRODUCTAPI = "http://localhost:8081/api/v1/product/{productId}";
    @Autowired
    public OnlineSaleController(OnlineSaleService onlineSaleService) {
        this.onlineSaleService = onlineSaleService;
    }

    @GetMapping(path = "/sale/{id}/product")
    public ResponseEntity<String> getProductBySale(@PathVariable("id") Long id)  {
        //check that the sale exists
        OnlineSale s = onlineSaleService.getOnlineSale(id);
        //if the sale exists get the product associated with the sale by ID
        if(s == null) {
            return new ResponseEntity<>("Invalid Sale Id", HttpStatus.FAILED_DEPENDENCY);
        }
        //query product endpoint with productID
        Map<String, Long> param = new HashMap<>();
        param.put("productId", s.getProductId());
        String result = restTemplate.getForObject(GETPRODUCTAPI, String.class, param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OnlineSale> addProduct(@RequestBody OnlineSale onlineSale) {
        return new ResponseEntity<>(onlineSaleService.addSale(onlineSale), HttpStatus.CREATED);
    }


}
