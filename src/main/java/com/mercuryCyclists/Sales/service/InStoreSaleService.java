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
    public ResponseEntity<InStoreSale> registerInStoreSale(InStoreSale inStoreSale, Long storeId) {
        // Validate sale
        if (!inStoreSale.validate()) {
            throw new IllegalStateException("Invalid In Store Sale");
        }

        // Do after first validation in case quantity is null
        if (inStoreSale.getQuantity() <= 0) {
            throw new IllegalStateException("In Store Sale has 0 or a negative quantity");
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
            return new ResponseEntity<>(inStoreSale, HttpStatus.CREATED);
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
                    return new ResponseEntity<>(inStoreSale, HttpStatus.SEE_OTHER);
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
            return new ResponseEntity<>(inStoreSale, HttpStatus.CREATED);
        }
    }

    /**
     * Add store to in store sale
     */
    public InStoreSale addStoreToInStoreSale(Long inStoreSaleId, Long storeId) {
        Optional<InStoreSale> inStoreSale = inStoreSaleRepository.findById(inStoreSaleId);
        if (!inStoreSale.isPresent()) {
            throw new IllegalStateException(String.format("In Store Sale with Id %s does not exist", inStoreSaleId));
        }

        Optional<Store> store = storeRepository.findById(storeId);
        if (!store.isPresent()) {
            throw new IllegalStateException(String.format("Store with Id %s does not exist", storeId));
        }

        InStoreSale inStoreSaleObj = inStoreSale.get();
        Store storeObj = store.get();

        inStoreSaleObj.setStore(storeObj);
        inStoreSaleRepository.save(inStoreSaleObj);
        return inStoreSaleObj;
    }

    /**
     * Updates existing in store sale based on the in store sale given
     */
    public InStoreSale updateInStoreSale(InStoreSale inStoreSale, Long inStoreSaleId) {
        if (!inStoreSale.validate()) {
            throw new IllegalStateException("Invalid in store sale");
        }

        JsonObject product = saleService.getSaleProduct(inStoreSale);
        if(product.get("id") == null){
            throw new IllegalArgumentException(String.format("Invalid product, %s", product));
        }

        Optional<InStoreSale> existingInSoreSale = inStoreSaleRepository.findById(inStoreSaleId);
        if (!existingInSoreSale.isPresent()) {
            throw new IllegalStateException(String.format("in store sale with Id %s does not exist", inStoreSaleId));
        }

        inStoreSaleRepository.save(inStoreSale);
        return inStoreSale;
    }

    /**
     * Deletes existing in store sale based on ID
     */
    public void deleteInStoreSale(Long inStoreSaleId) {
        Optional<InStoreSale> existingInSoreSale = inStoreSaleRepository.findById(inStoreSaleId);
        if (!existingInSoreSale.isPresent()) {
            throw new IllegalStateException(String.format("in store sale with Id %s does not exist", inStoreSaleId));
        }

        inStoreSaleRepository.delete(existingInSoreSale.get());
    }

    // TODO: DELETE
    public InStoreSale getTest(Long aLong) {
        Optional<InStoreSale> inStoreSale = inStoreSaleRepository.findById(aLong);
        JsonArray productParts = saleService.getSaleProductParts(inStoreSale.get());

        System.out.println("in get test");
        System.out.println(productParts);

        System.out.println(productParts.get(0));
        System.out.println(productParts.get(1));

        JsonObject t = productParts.get(0).getAsJsonObject();
        t.addProperty("description", "ahahhahahahahahahahahhahahahahha");

        System.out.println(t);

        saleService.updateProductPart(t);

        return inStoreSale.get();
    }
}
