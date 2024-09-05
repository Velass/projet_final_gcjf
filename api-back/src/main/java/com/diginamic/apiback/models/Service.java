package com.diginamic.apiback.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@lombok.Getter
@lombok.Setter
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<User> user = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private Organization organization;

    private String lastName;

    public Service() {

    }
}
