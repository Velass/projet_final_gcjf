package com.diginamic.apiback.models;

import com.diginamic.apiback.dto.AbsenceDTO;
import com.diginamic.apiback.enums.*;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter

public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long user_id;

    private Date dtDebut;

    private Date dt_fin;

    @Enumerated(EnumType.STRING)
    private AbsenceType type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String motif;

    public Absence() {

    }

    @Override
    public String toString() {
        return "Absence [id=" + id + ", user_id=" + user_id + ", dtDebut=" + dtDebut + ", dt_fin="
                + dt_fin + ", type=" + type + ", status=" + status + ", motif=" + motif + "]";
    }

    public AbsenceDTO toDto() {
        AbsenceDTO absenceDTO = new AbsenceDTO();
        absenceDTO.setDtDebut(dtDebut);
        absenceDTO.setDt_fin(dt_fin);
        absenceDTO.setId(id);
        absenceDTO.setMotif(motif);
        absenceDTO.setStatus(status);
        absenceDTO.setType(type);
        absenceDTO.setFullName(user.getFirstName() + " " + user.getLastName());
        return absenceDTO;
    }
    
    public AbsenceDTO toDtoWOFullName() {
        AbsenceDTO absenceDTO = new AbsenceDTO();
        absenceDTO.setDtDebut(dtDebut);
        absenceDTO.setDt_fin(dt_fin);
        absenceDTO.setId(id);
        absenceDTO.setMotif(motif);
        absenceDTO.setStatus(status);
        absenceDTO.setType(type);
        return absenceDTO;
    }

}
