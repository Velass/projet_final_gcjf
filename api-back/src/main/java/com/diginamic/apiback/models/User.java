package com.diginamic.apiback.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.diginamic.apiback.dto.AbsenceDTO;
import com.diginamic.apiback.dto.UserDTO;
import com.diginamic.apiback.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
@lombok.Getter
@lombok.Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "manager")
    @Column(nullable = true)
    private List<User> users = new ArrayList<>();

    @ManyToOne
    private User manager;

    @ManyToOne
    private Service service;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Absence> absences = new ArrayList<>();

    private String firstName;

    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Size(min = 3, message = "Le mot de passe doit contenir au moins 3 caractères")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Float rttEmployee;

    private Float unpaidLeave;

    private Float paidLeave;

    public User() {

    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", role=" + role + ", rttEmployee=" + rttEmployee + ", unpaidLeave=" + unpaidLeave
                + ", paidLeave=" + paidLeave + "]";
    }

    public UserDTO toDto() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setEmail(email);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setManager(manager);
        userDTO.setRole(role);
        userDTO.setPaidLeave(paidLeave);
        userDTO.setRttEmployee(rttEmployee);
        userDTO.setUnpaidLeave(unpaidLeave);
        userDTO.setService(service);
        List<AbsenceDTO> absenceDTOs = new ArrayList<>();
        for (Absence abs : absences) {
            absenceDTOs.add(abs.toDtoWOFullName());
        }
        userDTO.setAbsences(absenceDTOs);

        return userDTO;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(role);

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
