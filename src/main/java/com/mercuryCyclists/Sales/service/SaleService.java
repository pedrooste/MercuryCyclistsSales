package com.mercuryCyclists.Sales.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercuryCyclists.Sales.entity.Sale;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
    private static final String UPDATEPRODUCT = "http://localhost:8081/api/v1/product/{productId}";
    private static final String UPDATEPART = "http://localhost:8081/api/v1/product/part/{partId}";
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
    JsonArray getSaleProductParts(Sale sale){

        Map<String, String> params = new HashMap<>();
        params.put("productId", sale.getProductId().toString());

        try {
            String productResponse = restTemplate.getForObject(GETPRODUCTPARTS, String.class, params);
            return jsonParser.parse(productResponse).getAsJsonArray();
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to get product parts with id: %d, exception: %s", sale.getProductId(), exception));
        }
    }

    /**
     * Updates a product from a product JsonObject
     */
    void updateProduct(JsonObject product) {
        Map<String, String> params = new HashMap<>();
        params.put("productId", product.get("id").toString());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(product.toString(), headers);
            restTemplate.exchange(UPDATEPRODUCT, HttpMethod.PUT, entity, String.class, params);
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to update product with id: %d, exception: %s", product.get("id").getAsLong(), exception));
        }
    }

    /**
     * Updates a product's part from a part JsonObject
     */
    void updateProductPart(JsonObject part) {
        Map<String, String> params = new HashMap<>();
        params.put("partId", part.get("id").toString());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(part.toString(), headers);
            restTemplate.exchange(UPDATEPART, HttpMethod.PUT, entity, String.class, params);
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to update part with id: %d, exception: %s", part.get("id").getAsLong(), exception));
        }
    }
}