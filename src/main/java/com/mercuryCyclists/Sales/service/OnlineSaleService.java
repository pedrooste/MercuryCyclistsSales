package com.mercuryCyclists.Sales.service;

import com.mercuryCyclists.Sales.controller.OnlineSaleController;
import com.mercuryCyclists.Sales.entity.InStoreSale;
import com.mercuryCyclists.Sales.entity.OnlineSale;
import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.repository.InStoreSaleRepository;
import com.mercuryCyclists.Sales.repository.OnlineSaleRepository;
import com.mercuryCyclists.Sales.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OnlineSaleService {

    private final OnlineSaleRepository onlineSaleRepository;

    @Autowired
    OnlineSaleService(OnlineSaleRepository onlineSaleRepository) {
        this.onlineSaleRepository = onlineSaleRepository;
    }

    public OnlineSale getOnlineSale(Long saleId) {
        Optional<OnlineSale> sale = onlineSaleRepository.findById(saleId);

        if (!sale.isPresent()) {
            throw new IllegalStateException(String.format("Sale with Id %s does not exist", saleId));
        }

        return sale.get();
    }
    /*
        Get Product By Sale
     */

    public OnlineSale addSale(OnlineSale sale) {
        onlineSaleRepository.save(sale);
        return sale;
    }

}
