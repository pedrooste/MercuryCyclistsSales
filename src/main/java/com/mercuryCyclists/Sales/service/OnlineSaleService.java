package com.mercuryCyclists.Sales.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.repository.OnlineSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for online sale
 */

@Service
public class OnlineSaleService {

    private final OnlineSaleRepository onlineSaleRepository;
    private final SaleService saleService;

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
}
