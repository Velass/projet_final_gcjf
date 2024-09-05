package com.diginamic.apiback.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.diginamic.apiback.dto.AbsenceDTO;
import com.diginamic.apiback.dto.UserDTO;
import com.diginamic.apiback.models.User;
import com.diginamic.apiback.services.AbsenceService;
import com.diginamic.apiback.services.ServiceService;
import com.diginamic.apiback.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private ServiceService serviceService;

    /**
     * Route pour récupérer tous les utilisateurs (version Manager)
     * 
     * @return une liste d'utilisateurs
     */
    @GetMapping("/all")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    /**
     * Route pour récupérer un utilisateur par son id
     * 
     * @param authentication l'authentification
     * @return un utilisateur
     */
    @GetMapping()
    public ResponseEntity<?> findById(Authentication authentication) {
        final User user = userService.loadUserByUsername(authentication.getName());
        Long id = user.getId();
        if (user.isCredentialsNonExpired()) {
            return ResponseEntity.ok(userService.findById(id).get().toDto());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
     * Route pour récupérer toutes les absences
     * 
     * @param authentication l'authentification
     * @return une liste d'absences
     */
    @GetMapping("/absence")
    public List<AbsenceDTO> getAbsencesForUser(Authentication authentication) {
        final User user = userService.loadUserByUsername(authentication.getName());
        Long id = user.getId();

        return absenceService.findAbscenceForUserId(id);
    }

    /**
     * Route pour créer une absence
     * 
     * @param id l'ID de l'utilisateur
     * @return une absence
     */
    @DeleteMapping("/delete/{id}")
    public User deleteUser(@NonNull @PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * Route pour récupérer les utilisateurs d'un service
     * 
     * @param id l'ID du service
     * @return une liste d'utilisateurs
     */
    @GetMapping("/service/{id}")
    public ResponseEntity<?> findByServiceId(@NonNull @PathVariable Long id) {
        Optional<com.diginamic.apiback.models.Service> service = serviceService.findById(id);
        if (service.isPresent()) {
            List<UserDTO> userDtos = new ArrayList<>();
            for (User user : userService.findByService(service.get())) {
                userDtos.add(user.toDto());
            }
            return ResponseEntity.ok(userDtos);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "the service you are looking for does not exists"));
    }

}
