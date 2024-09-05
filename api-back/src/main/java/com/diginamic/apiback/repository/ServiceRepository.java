package com.diginamic.apiback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diginamic.apiback.models.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

}
