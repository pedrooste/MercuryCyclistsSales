package com.mercuryCyclists.Sales.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.repository.InStoreSaleRepository;
import com.mercuryCyclists.Sales.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for in store sale
 */

@Service
public class InStoreSaleService {

    private final InStoreSaleRepository inStoreSaleRepository;
    private final StoreRepository storeRepository;
    private final SaleService saleService;

    @Autowired
    public InStoreSaleService(InStoreSaleRepository inStoreSaleRepository, StoreRepository storeRepository, SaleService saleService) {
        this.inStoreSaleRepository = inStoreSaleRepository;
        this.storeRepository = storeRepository;
        this.saleService = saleService;
    }

    /**
     * Gets all In Store Sales
     */
    public List<InStoreSale> getAllInStoreSales() {
        return inStoreSaleRepository.findAll();
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
}
