package com.mercuryCyclists.Sales.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.repository.InStoreSaleRepository;
import com.mercuryCyclists.Sales.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for in store sale
 */

@Service
public class InStoreSaleService {

    private final InStoreSaleRepository inStoreSaleRepository;
    private final StoreRepository storeRepository;
    private final SaleService saleService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static RestTemplate restTemplate = new RestTemplate();
    private static final String GETPRODUCTAPI = "http://localhost:8081/api/v1/product/{productId}";

    @Autowired
    public InStoreSaleService(InStoreSaleRepository inStoreSaleRepository, StoreRepository storeRepository, SaleService saleService, KafkaTemplate<String, String> kafkaTemplate) {
        this.inStoreSaleRepository = inStoreSaleRepository;
        this.storeRepository = storeRepository;
        this.saleService = saleService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Gets all In Store Sales
     */
    public List<InStoreSale> getAllInStoreSales() {
        return inStoreSaleRepository.findAll();
    }

    /**
     * Get sale by id
     */
    public InStoreSale getInstoreSale(Long id) {

        Optional<InStoreSale> s = inStoreSaleRepository.findById(id);
        if(!s.isPresent()) {
            throw new IllegalArgumentException("Sale with ID does not exist");
        }
        return s.get();
    }

    /**
     * Get Product by sale
     */
    public ResponseEntity<String> getProductBySaleId(InStoreSale s) {
        if(s == null) {
            return new ResponseEntity<>("Invalid Sale Id", HttpStatus.FAILED_DEPENDENCY);
        }
        //query product endpoint with productID
        Map<String, Long> param = new HashMap<>();
        param.put("productId", s.getProductId());
        String result = restTemplate.getForObject(GETPRODUCTAPI, String.class, param);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Gets In Store Sales by Store ID
     */
    public List<InStoreSale> getStoreSales(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);

        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        return inStoreSaleRepository.findInStoreSalesByStore(store.get());
    }

    /**
     * Register a new In Store Sale
     */
    public ResponseEntity<String> registerInStoreSale(InStoreSale inStoreSale, Long storeId) {
        // Validate sale
        if (!inStoreSale.validate()) {
            throw new IllegalStateException("Invalid In Store Sale");
        }

        // Check if store exists
        Optional<Store> store = storeRepository.findById(storeId);
        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        // Check product exists
        JsonObject product = saleService.getSaleProduct(inStoreSale);
        if(product.get("id") == null){
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }

        // If there is enough of the product is stock
        Long productQuantity = product.get("quantity").getAsLong();
        Long saleQuantity = inStoreSale.getQuantity();
        if (productQuantity >= saleQuantity) {
            // Update and save product
            product.addProperty("quantity", (productQuantity - saleQuantity));
            saleService.updateProduct(product);

            // Save and return sale
            inStoreSale.setStore(store.get());
            inStoreSaleRepository.save(inStoreSale);
            return new ResponseEntity<>(inStoreSale.toString(), HttpStatus.CREATED);
        } else {
            // Get product parts
            JsonArray productParts = saleService.getSaleProductParts(inStoreSale);
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
                    return new ResponseEntity<>("api/v1/online/in-store-sale/backorder", HttpStatus.SEE_OTHER);
                }
            }

            // For each json object part in json object part array list
            // Save the json object part
            for (JsonObject part : partJsonObjs) {
                saleService.updateProductPart(part);
            }

            // Save and return sale
            inStoreSale.setStore(store.get());
            inStoreSaleRepository.save(inStoreSale);
            return new ResponseEntity<>(inStoreSale.toString(), HttpStatus.CREATED);
        }
    }

    /**
     * Registers backorder
     * Validates the sale
     * Gets the sale's product, validates it exists
     * saves online sale and publishes to kafka
     * @param inStoreSale backorder to be saved
     */
    public InStoreSale registerBackorder(InStoreSale inStoreSale, Long storeId){
        if (!inStoreSale.validate()) {
            throw new IllegalStateException("Invalid online sale");
        }

        Optional<Store> store = storeRepository.findById(storeId);
        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        JsonObject product = saleService.getSaleProduct(inStoreSale);
        if(product.get("id") == null){
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }

        inStoreSaleRepository.save(inStoreSale);

        String msg = new Gson().toJson(inStoreSale);
        kafkaTemplate.send("backorder", msg);

        return inStoreSale;
    }
}
