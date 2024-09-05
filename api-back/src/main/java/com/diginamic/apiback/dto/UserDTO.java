package com.diginamic.apiback.dto;

import java.util.ArrayList;
import java.util.List;

import com.diginamic.apiback.enums.Role;
import com.diginamic.apiback.models.Service;
import com.diginamic.apiback.models.User;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private List<User> users = new ArrayList<>();
    private User manager;
    private Service service;
    private List<AbsenceDTO> absences = new ArrayList<>();
    private String firstName;
    private String lastName;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Float rttEmployee;
    private Float unpaidLeave;
    private Float paidLeave;

    public UserDTO() {
    }

    @Override
    public String toString() {
        return "UserDTO [id=" + id + ", users=" + users + ", manager=" + manager + ", service=" + service
                + ", absences=" + absences + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", role=" + role + ", rttEmployee=" + rttEmployee + ", unpaidLeave=" + unpaidLeave
                + ", paidLeave=" + paidLeave + "]";
    }

}
