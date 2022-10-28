package com.mercuryCyclists.Sales.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.SaleEvent;
import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.repository.InStoreSaleRepository;
import com.mercuryCyclists.Sales.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cloud.stream.function.StreamBridge;

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
    private final StreamBridge streamBridge;


    @Autowired
    public InStoreSaleService(InStoreSaleRepository inStoreSaleRepository, StoreRepository storeRepository,
                              SaleService saleService, KafkaTemplate<String, String> kafkaTemplate,
                              StreamBridge streamBridge) {
        this.inStoreSaleRepository = inStoreSaleRepository;
        this.storeRepository = storeRepository;
        this.saleService = saleService;
        this.kafkaTemplate = kafkaTemplate;
        this.streamBridge = streamBridge;
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
    public InStoreSale getInstoreSale(UUID id) {

        Optional<InStoreSale> s = inStoreSaleRepository.findById(id);
        if (!s.isPresent()) {
            throw new IllegalArgumentException("Sale with ID does not exist");
        }
        return s.get();
    }

    /**
     * Get Product by sale
     */
    public ResponseEntity<String> getProductBySaleId(InStoreSale s) {
        if (s == null) {
            return new ResponseEntity<>("Invalid Sale Id", HttpStatus.FAILED_DEPENDENCY);
        }
        //query product endpoint with productID
        return new ResponseEntity<>(saleService.getSaleProduct(s).toString(), HttpStatus.OK);
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

        JsonObject product = saleService.getSaleProduct(inStoreSale);
        if (product == null) return new ResponseEntity<>("api/v1/online/in-store-sale/backorder", HttpStatus.SEE_OTHER);
        if (product.get("quantity").getAsLong() >= inStoreSale.getQuantity()) {
            product.addProperty("quantity", (product.get("quantity").getAsLong() - inStoreSale.getQuantity()));
            saleService.updateProduct(product);
        } else {
            JsonArray productParts = saleService.getSaleProductPartsWithQuantity(inStoreSale);
            if (productParts == null) {
                return new ResponseEntity<>("api/v1/online/in-store-sale/backorder", HttpStatus.SEE_OTHER);
            }
            for (JsonElement part : productParts) {
                JsonObject partJsonObj = part.getAsJsonObject();
                partJsonObj.addProperty("quantity", partJsonObj.get("quantity").getAsLong() - inStoreSale.getQuantity());
                saleService.updateProductPart(product.get("id").getAsString(), partJsonObj);
            }
        }
        inStoreSale.setStore(store.get());
        inStoreSaleRepository.save(inStoreSale);

        streamBridge.send("sale-outbound",
                saleService.createSaleEvent(inStoreSale,
                    product.get("name").getAsString(),
                    product.get("price").getAsDouble()));

        return new ResponseEntity<>(inStoreSale.toString(), HttpStatus.CREATED);
    }

    /**
     * Registers backorder
     * Validates the sale
     * Gets the sale's product, validates it exists
     * saves online sale and publishes to kafka
     *
     * @param inStoreSale backorder to be saved
     */
    public InStoreSale registerBackorder(InStoreSale inStoreSale, Long storeId) {
        if (!inStoreSale.validate()) {
            throw new IllegalStateException("Invalid online sale");
        }

        Optional<Store> store = storeRepository.findById(storeId);
        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        JsonObject product = saleService.getSaleProduct(inStoreSale);
        if (product.get("id") == null) {
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }

        inStoreSaleRepository.save(inStoreSale);

        streamBridge.send("sale-outbound",
                saleService.createSaleEvent(inStoreSale,
                    product.get("name").getAsString(),
                    product.get("price").getAsDouble()));

        String msg = new Gson().toJson(inStoreSale);
        kafkaTemplate.send("backorder", msg);

        return inStoreSale;
    }
}
