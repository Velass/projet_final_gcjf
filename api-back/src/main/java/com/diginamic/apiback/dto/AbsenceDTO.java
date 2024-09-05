package com.diginamic.apiback.dto;

import java.util.Date;

import com.diginamic.apiback.enums.AbsenceType;
import com.diginamic.apiback.enums.Status;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbsenceDTO {
    private Long id;
    private Date dtDebut;
    private Date dt_fin;
    private String fullName;
    private AbsenceType type;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String motif;
}
