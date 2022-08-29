package com.mercuryCyclists.Sales.controller;

import com.mercuryCyclists.Sales.entity.Contact;
import com.mercuryCyclists.Sales.service.SupplierService;
import com.mercuryCyclists.Sales.entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for supplier
 */
@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Gets all suppliers
     */
    @GetMapping
    public List<Supplier> getSuppliers() {
        return supplierService.getAllSuppliers();
    }

    /**
     * Gets supplier based on Id
     */
    @GetMapping(path = "{supplierId}")
    public Supplier getSupplier(@PathVariable("supplierId") Long supplierId) {
        return supplierService.getSupplier(supplierId);
    }

    /**
     * Registers a new supplier
     */
    @PostMapping()
    public Supplier registerSupplier(@RequestBody Supplier supplier) {
        return supplierService.registerSupplier(supplier);
    }

    /**
     * Method to add a contact to a supplier
     */
    @PostMapping("/{supplierId}/contact")
    public Supplier addContact(@PathVariable("supplierId") Long supplierId, @RequestBody Contact contact) {
        return supplierService.addContact(supplierId, contact);
    }

    /**
     * Updates existing supplier based on the supplier given
     */
    @PutMapping()
    public Supplier updateSupplier(@RequestBody Supplier supplier) {
        return supplierService.updateSupplier(supplier);
    }


    /**
     * Method to update a contact for a supplier
     */
    @PutMapping("/{supplierId}/contact")
    public Supplier updateContact(@PathVariable("supplierId") Long supplierId, @RequestBody Contact contact) {
        return supplierService.updateContact(supplierId, contact);
    }

    /**
     * Deletes existing supplier based on Id
     */
    @DeleteMapping(path = "{supplierId}")
    public void deleteSupplier(@PathVariable("supplierId") Long supplierId) {
        supplierService.deleteSupplier(supplierId);
    }

    /**
     * deletes contact of a supplier
     */
    @DeleteMapping(path = "/{supplierId}/contact/{contactId}")
    public Supplier deleteSupplierContact(@PathVariable("supplierId") Long supplierId, @PathVariable("contactId") Long contactId) {
        return supplierService.deleteContact(supplierId, contactId);
    }
}
