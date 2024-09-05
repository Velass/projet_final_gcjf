package com.diginamic.apiback.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
@lombok.Getter
@lombok.Setter
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "organization")
    private List<SpecificAbsence> specificAbsence = new ArrayList<>();

    private String Name;

    private Float rtt_employer;

    private Float public_holiday;

    @OneToMany(mappedBy = "organization")
    private List<Service> services = new ArrayList<>();

    public Organization() {

    }

    @Override
    public String toString() {
        return "Organization [id=" + id + ", specificAbsence=" + specificAbsence + ", Name=" + Name + ", rtt_employer="
                + rtt_employer + ", public_holiday=" + public_holiday + "]";
    }

}
