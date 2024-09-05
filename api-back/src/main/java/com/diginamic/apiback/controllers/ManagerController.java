package com.diginamic.apiback.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import com.diginamic.apiback.dto.UserDTO;
import com.diginamic.apiback.models.User;
import com.diginamic.apiback.services.AbsenceService;
import com.diginamic.apiback.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    @Autowired
    private UserService userService;

    @Autowired
    private AbsenceService absenceService;

    /**
     * Route pour récupérer tous les utilisateurs (version Manager)
     * 
     * @return une liste d'utilisateurs
     */
    @Secured("MANAGER")
    @GetMapping()
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    /**
     * Route pour récupérer un utilisateur par son id
     * 
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur
     */
    @Secured("MANAGER")
    @GetMapping("/{id}")
    public Optional<User> findById(@NonNull @PathVariable("id") Long id) {
        return userService.findById(id);
    }

    /**
     * Route pour mettre à jour un utilisateur
     * 
     * @param user l'utilisateur
     * @param id   l'ID de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    @PutMapping("/{id}")
    public User updateUser(@NonNull @RequestBody @Valid User user, @NonNull @PathVariable("id") Long id) {
        return userService.updateUser(user, id);
    }

    /**
     * Route pour supprimer un utilisateur
     * 
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur supprimé
     */
    @DeleteMapping("/delete/{id}")
    public User deleteUser(@NonNull @PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    /**
     * Route pour valider une absence (manager only)
     * 
     * @param id l'ID de l'absence
     * @return un message de confirmation
     */
    @Secured("MANAGER")
    @GetMapping("/absence/{id}/validate")
    public ResponseEntity<?> validateAbsence(@PathVariable Long id) {
        try {
            absenceService.validateAbsence(id);
            return ResponseEntity.ok().body(Map.of("message", "Absence validated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "The absence you are trying to validate does not exists"));
        }
    }

    /**
     * Route pour rejeter une absence (manager only)
     * 
     * @param id l'ID de l'absence
     * @return un message de confirmation
     */
    @Secured("MANAGER")
    @GetMapping("/absence/{id}/rejete")
    public ResponseEntity<?> rejeteAbsence(@PathVariable Long id) {
        try {
            absenceService.rejeteAbsence(id);
            return ResponseEntity.ok().body(Map.of("message", "Absence rejete successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "The absence you are trying to rejete does not exists"));
        }
    }

}
