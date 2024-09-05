package com.diginamic.apiback.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diginamic.apiback.enums.Status;
import com.diginamic.apiback.models.Absence;
import com.diginamic.apiback.repository.AbsenceRepository;

@RestController
@RequestMapping("/api/traitement-nuit")
public class TraitementNuitController {
    @Autowired
    private AbsenceRepository absenceRepository;

    @GetMapping("launch")
    public ResponseEntity<?> launchTraitementNuit() {
        List<Absence> absences = absenceRepository.findByStatus("INITIALE");

        for (Absence absence : absences) {
            absence.setStatus(Status.EN_ATTENTE_VALIDATION);
            absenceRepository.save(absence);
        }

        return ResponseEntity.ok().body(absences);
    }

}
