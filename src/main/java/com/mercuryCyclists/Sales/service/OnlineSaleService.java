package com.mercuryCyclists.Sales.service;

import com.google.gson.*;
import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.repository.OnlineSaleRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for online sale
 */

@Service
public class OnlineSaleService {

    private final OnlineSaleRepository onlineSaleRepository;
    private final SaleService saleService;
    private static final String POSTBACKORDER = "http://localhost:8081/api/v1/product/backorder";
    private static final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public OnlineSaleService(OnlineSaleRepository onlineSaleRepository, SaleService saleService) {
        this.onlineSaleRepository = onlineSaleRepository;
        this.saleService = saleService;
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
    public OnlineSale getOnlineSale(Long id) {

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

        // Check product exists
        JsonObject product = saleService.getSaleProduct(onlineSale);
        if(product.get("id") == null){
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }
        // If there is enough of the product is stock
        Long productQuantity = product.get("quantity").getAsLong();
        Long saleQuantity = onlineSale.getQuantity();
        if (productQuantity >= saleQuantity) {
            // Update and save product
            product.addProperty("quantity", (productQuantity - saleQuantity));
            saleService.updateProduct(product);

            // Save and return sale
            onlineSaleRepository.save(onlineSale);
            return new ResponseEntity<>(onlineSale.toString(), HttpStatus.CREATED);
        } else {
            // Get product parts
            JsonArray productParts = saleService.getSaleProductParts(onlineSale);
            ArrayList<JsonObject> partJsonObjs = new ArrayList<JsonObject>();

            // For each part in the product
            for (JsonElement part : productParts) {
                // Convert json element to json object
                JsonObject partJsonObj = part.getAsJsonObject();

                Long partQuantity = partJsonObj.get("quantity").getAsLong();
                if (partQuantity >= saleQuantity) {
                    // Update part quantity
                    partJsonObj.addProperty("quantity", (partQuantity - saleQuantity));

                    // Save to ArrayList of JsonObjects
                    partJsonObjs.add(partJsonObj);
                } else {
                    // return 303 Error
                    return new ResponseEntity<>("api/v1/online/online-sale/backorder", HttpStatus.SEE_OTHER);
                }
            }

            // For each json object part in json object part array list
            // Save the json object part
            for (JsonObject part : partJsonObjs) {
                saleService.updateProductPart(part);
            }

            // Save and return sale
            onlineSaleRepository.save(onlineSale);
            return new ResponseEntity<>(onlineSale.toString(), HttpStatus.CREATED);
        }
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
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();

        Map<String, JsonObject> m = new HashMap<>();
        String msg = new Gson().toJson(onlineSale);

        ResponseEntity<String> productResponse = null;
        try {
            productResponse = restTemplate.postForEntity(POSTBACKORDER, msg, String.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if(productResponse == null) {
            throw new IllegalStateException("Product response was empty, please check that procurement service is running and Kafka is running");
        }

        return onlineSale;
    }
}
