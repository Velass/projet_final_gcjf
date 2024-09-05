package com.diginamic.apiback.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import com.diginamic.apiback.models.Service;

import com.diginamic.apiback.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    ServiceRepository serviceRepository;

    /**
     * Trouve un service par son id.
     *
     * @param id l'id du service
     * @return un Optional de l'objet Service
     */
    public Optional<com.diginamic.apiback.models.Service> findById(@NonNull Long id) {
        return serviceRepository.findById(id);
    }

    /**
     * Trouve tous les services.
     *
     * @return une liste d'objets Service
     */
    public List<Service> findAll() {
        List<Service> services = serviceRepository.findAll();
        return services;
    }

}
