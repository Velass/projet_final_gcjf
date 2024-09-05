package com.diginamic.apiback.controllers;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.diginamic.apiback.dto.AbsenceDTO;
import com.diginamic.apiback.dto.UserDTO;
import com.diginamic.apiback.models.Absence;
import com.diginamic.apiback.models.SpecificAbsence;
import com.diginamic.apiback.models.User;
import com.diginamic.apiback.services.AbsenceService;
import com.diginamic.apiback.services.ServiceService;
import com.diginamic.apiback.services.SpecificAbsenceService;
import com.diginamic.apiback.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/absence")
public class AbsenceController {
    @Autowired
    AbsenceService absenceService;

    @Autowired
    UserService userService;

    @Autowired
    SpecificAbsenceService specificAbsenceService;

    @Autowired
    private ServiceService serviceService;

    /**
     * Route pour récupérer toutes les absences
     * 
     * @return une liste d'absences
     */
    @Secured("MANAGER")
    @GetMapping("/all")
    public ResponseEntity<?> findAll(Authentication authentication) {
        User user = userService.loadUserByUsername(authentication.getName());
        List<AbsenceDTO> absenceDtoList = new ArrayList<>();
        List<Absence> absenceList = absenceService.findAllForCurrentManager(user);
        for (Absence absence : absenceList) {
            absenceDtoList.add(absence.toDto());
        }

        return ResponseEntity.ok().body(absenceDtoList);
    }

    /**
     * Route pour récupérer une absence par son id
     * 
     * @param id l'ID de l'absence
     * @return l'absence
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@NonNull @PathVariable("id") Long id) {
        Optional<Absence> absence = absenceService.findById(id);
        if (absence.isPresent()) {
            return ResponseEntity.ok().body(absence.get().toDto());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "No absence entity with corresponding id found in db"));
    }

    /**
     * Route pour créer une absence
     * 
     * @param absence l'absence
     * @return l'absence créée
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAbsence(@RequestBody @Valid Absence absence) {
        Absence abs = absenceService.createAbsence(absence);
        return ResponseEntity.ok().body(abs.toDto());
    }

    /**
     * Route pour mettre à jour une absence
     * 
     * @param authentication l'authentification
     * @param absence        l'absence
     * @param id             l'ID de l'absence
     * @return un message de confirmation
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(Authentication authentication, @NonNull @RequestBody @Valid Absence absence,
            @NonNull @PathVariable("id") Long id) {
        try {
            absenceService.updateAbsence(authentication, absence, id);
            return ResponseEntity.ok().body(Map.of("message", "Your absence was updated successfuly"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "The absence you are trying to update does not exists"));
        }
    }

    /**
     * Route pour supprimer une absence
     * 
     * @param id l'ID de l'absence
     * @return un message de confirmation
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAbsence(@NonNull @PathVariable("id") Long id) {
        Optional<Absence> abs = absenceService.findById(id);
        if (abs.isPresent()) {
            absenceService.deleteAbsence(id);
            return ResponseEntity.ok().body(Map.of("message", "Your absence was deleted successfuly"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "No absence entity with corresponding id found in db"));
    }

    /**
     * Route pour récupérer les absences d'un utilisateur
     * 
     * @param id    l'ID de l'utilisateur
     * @param month le mois
     * @param year  l'année
     * @return une liste d'absences
     */
    @Secured("MANAGER")
    @GetMapping("/service")
    public ResponseEntity<?> findAbsenceWithServiceMonthAndYear(@RequestParam Long id,
            @RequestParam String month,
            @RequestParam String year) {

        List<User> userList = userService.findByService(serviceService.findById(id).get());
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : userList) {
            List<Absence> absencesUserList = absenceService.findAbsenceMonthYear(user.getId(), month, year);
            Long organizationId = user.getService().getOrganization().getId();
            for (SpecificAbsence specificAbsence : specificAbsenceService.findAbsencesAndMonthAndYear(organizationId,
                    month, year)) {
                absencesUserList.add(specificAbsence.toAbsence());
            }
            user.setAbsences(absencesUserList);
            userDTOs.add(user.toDto());
        }

        return ResponseEntity.ok(userDTOs);
    }

}
