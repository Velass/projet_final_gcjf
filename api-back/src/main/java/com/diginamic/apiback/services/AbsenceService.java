package com.diginamic.apiback.services;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.diginamic.apiback.dto.AbsenceDTO;
import com.diginamic.apiback.enums.AbsenceType;
import com.diginamic.apiback.enums.Status;
import com.diginamic.apiback.models.Absence;
import com.diginamic.apiback.models.User;
import com.diginamic.apiback.repository.AbsenceRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class AbsenceService {

    @Autowired
    AbsenceRepository absenceRepository;

    @Autowired
    UserService userService;

    @Autowired
    ServiceService serviceService;

    /**
     * Trouve toutes les absences.
     *
     * @return une liste d'objets Absence
     */
    public List<Absence> findAll() {
        return absenceRepository.findAll();
    }

    /**
     * Trouve toutes les absences pour le current manager.
     *
     * @return une liste d'objets Absence
     */
    public List<Absence> findAllForCurrentManager(User user) {
        return absenceRepository.findByManagerId(user);
    }

    /**
     * Trouve les absences pour un utilisateur spécifique.
     *
     * @param id l'ID de l'utilisateur
     * @return une liste d'objets AbsenceDTO
     * @throws EntityNotFoundException si l'utilisateur n'est pas trouvé
     */
    public List<AbsenceDTO> findAbscenceForUserId(@NonNull Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            List<AbsenceDTO> absenceDTOs = new ArrayList<>();
            for (Absence abs : absenceRepository.findByUserOrderByDtDebutDesc(user.get())) {
                absenceDTOs.add(abs.toDto());
            }
            return absenceDTOs;
        }

        throw new EntityNotFoundException("L'utilisateur recherché n'a pas été trouvé");
    }

    /**
     * Trouve les utilisateurs pour un service spécifique.
     *
     * @param id l'ID du service
     * @return une liste d'objets User
     * @throws EntityNotFoundException si le service n'est pas trouvé
     */
    public List<User> findUserForServiceId(@NonNull Long id) {
        Optional<com.diginamic.apiback.models.Service> service = serviceService.findById(id);
        if (service.isPresent()) {
            List<User> users = new ArrayList<>();
            for (User user : userService.findByService(service.get())) {
                users.add(user);
            }
            return users;
        }

        throw new EntityNotFoundException("L'utilisateur recherché n'a pas été trouvé");
    }

    /**
     * Trouve une absence par son id.
     *
     * @param id l'id de l'absence
     * @return un Optional de l'objet Absence
     */
    public Optional<Absence> findById(@NonNull Long id) {
        return absenceRepository.findById(id);
    }

    /**
     * Met à jour une absence.
     *
     * @param authentication l'authentification de l'utilisateur
     * @param absence        l'absence à mettre à jour
     * @param id             l'ID de l'absence à mettre à jour
     * @return l'absence mise à jour
     * @throws EntityNotFoundException si l'absence n'est pas trouvée
     */
    public Absence updateAbsence(Authentication authentication, @Valid @NonNull Absence absence, @NonNull Long id) {
        boolean idAbsence = absenceRepository.existsById(id);
        if (idAbsence != true) {
            throw new EntityNotFoundException("cette absence n'existe pas");
        }
        absence.setId(id);
        absence.setStatus(Status.INITIALE);
        absence.setUser(userService.findByEmail(authentication.getName()).get());
        return absenceRepository.save(absence);
    }

    /**
     * Crée une absence.
     *
     * @param absence l'absence à créer
     * @return l'absence créée
     * @throws EntityNotFoundException si l'utilisateur n'est pas trouvé
     */
    public Absence createAbsence(@Valid Absence absence) {
        Optional<User> userOptional = userService.findById(absence.getUser_id());
        if (userOptional.isPresent()) {
            User userObject = userOptional.get();
            absence.setUser(userObject);
            return absenceRepository.save(absence);
        }

        throw new EntityNotFoundException("User not found");
    }

    /**
     * Supprime une absence.
     *
     * @param id l'ID de l'absence à supprimer
     * @return l'absence supprimée
     * @throws EntityNotFoundException si l'absence n'est pas trouvée
     */
    public Absence deleteAbsence(@NonNull Long id) {
        Absence absenceToDelete = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID : " + id + " introuvable"));
        if (absenceToDelete != null) {
            absenceRepository.deleteById(id);
        }
        return absenceToDelete;
    }

    /**
     * Trouve les absences pour un utilisateur spécifique, un mois et une année
     * spécifiques.
     *
     * @param user_id l'ID de l'utilisateur
     * @param month   le mois
     * @param year    l'année
     * @return une liste d'objets Absence
     */
    public List<Absence> findAbsenceMonthYear(Long user_id, String month, String year) {
        return absenceRepository.findAbsencesAndMonthAndYear(user_id, month, year);
    }

    /**
     * Récupère les absences pour une année et un mois spécifiques.
     *
     * @param user  l'utilisateur
     * @param year  l'année
     * @param month le mois
     * @return une réponse HTTP
     */
    public ResponseEntity<?> getAbsenceForYearAndMonth(User user, int year, int month) {
        return ResponseEntity.ok("Succès");
    }

    /**
     * Valide une absence.
     *
     * @param id l'ID de l'absence à valider
     * @throws EntityNotFoundException si l'absence n'est pas trouvée
     */
    @Transactional
    public void validateAbsence(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID : " + id + " introuvable"));
        User user = userService.findById(absence.getUser().getId()).get();
        Date startDate = absence.getDtDebut();
        Date endDate = absence.getDt_fin();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(startDate);
        cal2.setTime(endDate);

        int numberOfDays = 0;
        while (cal1.before(cal2)) {
            if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK))
                    && (Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK))) {
                numberOfDays++;
            }
            cal1.add(Calendar.DATE, 1);
        }

        if (absence.getType() == AbsenceType.PAID_LEAVE)
            user.setPaidLeave(user.getPaidLeave() - numberOfDays);
        if (absence.getType() == AbsenceType.RTT_EMPLOYEE)
            user.setRttEmployee(user.getRttEmployee() - numberOfDays);
        if (absence.getType() == AbsenceType.UNPAID_LEAVE)
            user.setUnpaidLeave(user.getUnpaidLeave() + numberOfDays);
        userService.save(user);

        absence.setStatus(Status.VALIDEE);
        absenceRepository.save(absence);
    }

    /**
     * Rejette une absence.
     *
     * @param id l'ID de l'absence à rejeter
     * @throws EntityNotFoundException si l'absence n'est pas trouvée
     */
    public void rejeteAbsence(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID : " + id + " introuvable"));
        absence.setStatus(Status.REJETEE);
        absenceRepository.save(absence);
    }

    public long remainingPaidLeaves(User user) {
        return 25 - absenceRepository.sumApprovedAbsences(user, AbsenceType.PAID_LEAVE);
    }

    public long remainingEmployeeWtr(User user) {
        return 6 - absenceRepository.sumApprovedAbsences(user, AbsenceType.RTT_EMPLOYEE);
    }

}
