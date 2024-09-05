package com.diginamic.apiback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diginamic.apiback.models.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

}
