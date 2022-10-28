package com.mercuryCyclists.Sales.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercuryCyclists.Sales.entity.Sale;
import com.mercuryCyclists.Sales.entity.SaleEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Service for abstract sale object
 */
@Service
public class SaleService {

    private static final String GETPRODUCT = "http://localhost:8081/api/v1/product/{productId}";
    private static final String GETPRODUCTPARTS = "http://localhost:8081/api/v1/product/{productId}/all-parts";
    private static final String UPDATEPRODUCT = "http://localhost:8081/api/v1/product/{productId}";
    private static final String UPDATEPART = "http://localhost:8081/api/v1/product/{productId}/part/{partId}";
    private static final String GETPRODUCTWITHQUANTITY = "http://localhost:8081/api/v1/product/{productId}/quantity/{quantity}";
    private static final String GETALLPARTSBYPRODUCTIDWITHQUANTITY = "http://localhost:8081/api/v1/product/{productId}/part/quantity/{quantity}";
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
     * if product does not exist or does not have enough quantity in stock return null
     * else return product
     * @param sale
     * @return Product if all conditions are satisfied else null
     */
    JsonObject getSaleProductWithQuantity(Sale sale) {
        Map<String, String> params = new HashMap<>();
        params.put("productId", sale.getProductId().toString());
        params.put("quantity", sale.getQuantity().toString());

        try {
            String productResponse = restTemplate.getForObject(GETPRODUCTWITHQUANTITY, String.class, params);
            if (productResponse != null)
                return jsonParser.parse(productResponse).getAsJsonObject();
            return null;
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to get product with id: %d, exception: %s", sale.getProductId(), exception));
        }
    }

    /**
     * get all parts of a product if the product exists and all the parts have sufficient stocks
     * @param sale
     * @return parts if all conditions are satisfied else null
     */
    JsonArray getSaleProductPartsWithQuantity(Sale sale) {
        Map<String, String> params = new HashMap<>();
        params.put("productId", sale.getProductId().toString());
        params.put("quantity", sale.getQuantity().toString());
        try {
            String productResponse = restTemplate.getForObject(GETALLPARTSBYPRODUCTIDWITHQUANTITY, String.class, params);
            if (productResponse != null)
                return jsonParser.parse(productResponse).getAsJsonArray();
            return null;
        } catch(Exception exception) {
            throw new IllegalArgumentException(String.format("Failed to get product parts with id: %d, exception: %s", sale.getProductId(), exception));        }
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
    void updateProductPart(String productId, JsonObject part) {
        Map<String, String> params = new HashMap<>();
        params.put("productId", productId);
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

    /**
     * Creates and returns a SaleEvent object
     */
    SaleEvent createSaleEvent(Sale sale, String productName, Double pricePerProduct) {
        SaleEvent saleEvent = new SaleEvent();
        saleEvent.setProductName(productName);
        saleEvent.setQuantity(sale.getQuantity());
        Double totalSalePrice = pricePerProduct * sale.getQuantity();
        saleEvent.setPrice(totalSalePrice);
        return saleEvent;
    }
}