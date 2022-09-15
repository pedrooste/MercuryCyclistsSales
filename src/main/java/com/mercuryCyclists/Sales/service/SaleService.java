package com.mercuryCyclists.Sales.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercuryCyclists.Sales.entity.Sale;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * Service for abstract sale object
 */
@Service
public class SaleService {

    private static final String GETPRODUCT = "http://localhost:8081/api/v1/product/{productId}";
    private static final String GETPRODUCTPARTS = "http://localhost:8081/api/v1/product/{productId}/all-parts";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final JsonParser jsonParser = new JsonParser();

    /**
     * Gets and returns product from sale
     */
    JsonObject getSaleProduct(Sale sale){

        Map<String, String> params = new HashMap<>();
        params.put("productId", sale.getProductId().toString());

        try {
            String productResponse = restTemplate.getForObject(GETPRODUCT, String.class, params);
            return jsonParser.parse(productResponse).getAsJsonObject();
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to get product with id: %d, exception: %s", sale.getProductId(), exception));
        }

    }

    /**
     * Gets and returns product parts from sale
     */
    JsonObject getSaleProductParts(Sale sale){

        Map<String, String> params = new HashMap<>();
        params.put("productId", sale.getProductId().toString());

        try {
            String productResponse = restTemplate.getForObject(GETPRODUCTPARTS, String.class, params);
            return jsonParser.parse(productResponse).getAsJsonObject();
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to get product parts with id: %d, exception: %s", sale.getProductId(), exception));
        }

    }
}