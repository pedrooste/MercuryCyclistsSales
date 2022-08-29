package com.mercuryCyclists.Sales.service;

import com.mercuryCyclists.Sales.entity.Contact;
import com.mercuryCyclists.Sales.entity.Supplier;
import com.mercuryCyclists.Sales.repository.ContactRepository;
import com.mercuryCyclists.Sales.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for supplier
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, ContactRepository contactRepository) {
        this.supplierRepository = supplierRepository;
        this.contactRepository = contactRepository;
    }

    /**
     * Gets all suppliers
     */
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    /**
     * Gets supplier based on id
     */
    public Supplier getSupplier(Long supplierId) {
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);

        if (!supplier.isPresent()) {
            throw new IllegalStateException(String.format("Supplier with Id %s does not exist", supplierId));
        }
        return supplier.get();
    }

    /**
     * Gets contact based on id
     */
    public Contact getContact(Long contactId) {
        Optional<Contact> contact = contactRepository.findById(contactId);

        if (!contact.isPresent()) {
            throw new IllegalStateException(String.format("Contact with Id %s does not exist", contactId));
        }
        return contact.get();
    }

    /**
     * Registers new supplier
     */
    public Supplier registerSupplier(Supplier supplier) {
        if (!supplier.validate()) {
            throw new IllegalStateException("Invalid supplier");

        }
        supplierRepository.save(supplier);
        return supplier;
    }

    /**
     * Updates existing supplier based on supplier given
     */
    public Supplier updateSupplier(Supplier supplier) {
        if (!supplier.validate()) {
            throw new IllegalStateException("Invalid supplier");

        }

        Optional<Supplier> existingSupplier = supplierRepository.findById(supplier.getId());
        if (!existingSupplier.isPresent()) {
            throw new IllegalStateException(String.format("Supplier with Id %s does not exist", supplier.getId()));
        }

        supplierRepository.save(supplier);
        return supplier;
    }

    /**
     * deletes existing supplier based on id
     */
    public void deleteSupplier(Long supplierId) {
        Optional<Supplier> existingSupplier = supplierRepository.findById(supplierId);

        if (!existingSupplier.isPresent()) {
            throw new IllegalStateException(String.format("Supplier with Id %s does not exist", supplierId));
        }
        supplierRepository.delete(existingSupplier.get());
    }

    /**
     * creates and adds contact to supplier
     */
    public Supplier addContact(Long supplierId, Contact contact) {
        if (!contact.validate()) {
            throw new IllegalStateException("Invalid contact");
        }

        Supplier existingSupplier = getSupplier(supplierId);
        existingSupplier.getContactSet().add(contact);

        supplierRepository.save(existingSupplier);
        return existingSupplier;
    }

    /**
     * updates contact of supplier
     */
    public Supplier updateContact(Long supplierId, Contact contact) {
        if (!contact.validate()) {
            throw new IllegalStateException("Invalid contact");
        }

        Supplier existingSupplier = getSupplier(supplierId);

        Optional<Contact> existingContact = contactRepository.findById(contact.getId());
        if (!existingContact.isPresent()) {
            throw new IllegalStateException(String.format("Contact with Id %s does not exist", contact.getId()));
        }

        contactRepository.save(contact);
        return existingSupplier;
    }

    /**
     * deletes contact of a supplier
     */
    public Supplier deleteContact(Long supplierId, Long contactId) {
        Supplier existingSupplier = getSupplier(supplierId);

        Contact existingContact = getContact(contactId);

        existingSupplier.getContactSet().remove(existingContact);
        supplierRepository.save(existingSupplier);
        return existingSupplier;
    }
}
