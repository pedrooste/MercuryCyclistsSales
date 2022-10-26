package com.mercuryCyclists.Sales.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.repository.OnlineSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for online sale
 */

@Service
public class OnlineSaleService {

    private final OnlineSaleRepository onlineSaleRepository;
    private final SaleService saleService;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    public OnlineSaleService(OnlineSaleRepository onlineSaleRepository, SaleService saleService, KafkaTemplate<String, String> kafkaTemplate) {
        this.onlineSaleRepository = onlineSaleRepository;
        this.saleService = saleService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Gets all Online Sales
     */
    public List<OnlineSale> GetOnlineSales() {
        return onlineSaleRepository.findAll();
    }
    /**
     * Get sale by id
     */
    public OnlineSale getOnlineSale(UUID id) {

        Optional<OnlineSale> s = onlineSaleRepository.findById(id);
        if(!s.isPresent()) {
            throw new IllegalArgumentException("Sale with ID does not exist");
        }
        return s.get();
    }

    /**
     * Get Product by sale
     */

    public ResponseEntity<String> getProductBySaleId(OnlineSale s) {
        if(s == null) {
            return new ResponseEntity<>("Invalid Sale Id", HttpStatus.FAILED_DEPENDENCY);
        }
        //query product endpoint with productID
        return new ResponseEntity<>(saleService.getSaleProduct(s).toString(), HttpStatus.OK);
    }

    /**
     * Register a new Online Sale
     */
    public ResponseEntity<String> registerOnlineSale(OnlineSale onlineSale) {
        // Validate sale

        if (!onlineSale.validate()) {
            throw new IllegalStateException("Invalid Online Sale");
        }
        JsonObject product = saleService.getSaleProductWithQuantity(onlineSale);
        if (product != null) {
            product.addProperty("quantity", (product.get("quantity").getAsLong() - onlineSale.getQuantity()));
            saleService.updateProduct(product);

            // Save and return sale
            onlineSaleRepository.save(onlineSale);
            // public sale to sales-topic
            kafkaTemplate.send("sales", new Gson().toJson(onlineSale));
            return new ResponseEntity<>(onlineSale.toString(), HttpStatus.CREATED);
        }
        product = saleService.getSaleProduct(onlineSale);
        if (product == null) return new ResponseEntity<>("api/v1/online/in-store-sale/backorder", HttpStatus.SEE_OTHER);
        JsonArray productParts = saleService.getSaleProductPartsWithQuantity(onlineSale);
        if (productParts == null) {
            return new ResponseEntity<>("api/v1/online/in-store-sale/backorder", HttpStatus.SEE_OTHER);
        }
        for (JsonElement part : productParts) {
            JsonObject partJsonObj = part.getAsJsonObject();
            partJsonObj.addProperty("quantity", partJsonObj.get("quantity").getAsLong() - onlineSale.getQuantity());
            saleService.updateProductPart(product.get("id").getAsString(), partJsonObj);
        }
        // public sale to sales-topic

        kafkaTemplate.send("sales", new Gson().toJson(onlineSale));
        onlineSaleRepository.save(onlineSale);
        return new ResponseEntity<>(onlineSale.toString(), HttpStatus.CREATED);
    }

    /**
     * Registers backorder
     * Validates the sale
     * Gets the sale's product, validates it exists
     * saves online sale and publishes to kafka
     * @param onlineSale backorder to be saved
     */
    public OnlineSale registerBackorder(OnlineSale onlineSale){
        if (!onlineSale.validate()) {
            throw new IllegalStateException("Invalid online sale");
        }

        JsonObject product = saleService.getSaleProduct(onlineSale);
        if(product.get("id") == null){
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }

        onlineSaleRepository.save(onlineSale);
        kafkaTemplate.send("sales", new Gson().toJson(onlineSale));

        String msg = new Gson().toJson(onlineSale);
        kafkaTemplate.send("backorder", msg);

        return onlineSale;
    }
}
