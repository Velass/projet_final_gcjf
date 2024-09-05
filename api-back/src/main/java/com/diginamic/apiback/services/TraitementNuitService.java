package com.diginamic.apiback.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.diginamic.apiback.enums.AbsenceType;
import com.diginamic.apiback.enums.Status;
import com.diginamic.apiback.models.Absence;
import com.diginamic.apiback.models.SpecificAbsence;
import com.diginamic.apiback.repository.AbsenceRepository;

@Service
public class TraitementNuitService {

    private SpecificAbsenceService specificAbsenceService;

    private AbsenceService absenceService;

    private AbsenceRepository absenceRepository;

    /**
     * Constructeur
     * 
     * @param absenceRepository
     * @param specificAbsenceService
     * @param absenceService
     */
    public TraitementNuitService(AbsenceRepository absenceRepository, SpecificAbsenceService specificAbsenceService,
            AbsenceService absenceService) {
        this.specificAbsenceService = specificAbsenceService;
        this.absenceService = absenceService;
        this.absenceRepository = absenceRepository;
    }

    /**
     * Méthode permettant de lancer le traitement de nuit
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void launchTraitementNuit() {

        // Chercher les absences en statut INITIALE
        List<Absence> absences = absenceRepository.findByStatus("INITIALE");
        System.out.println("Traitement de " + absences.size() + " absences");

        // Pour chaque absence
        for (Absence absence : absences) {
            System.out.println("Traitement de l'absence : " + absence.getId());

            // Si l'utilisateur a assez de jours
            if (aAssezDeJours(absence)) {
                // On met l'absence en attente de validation
                absence.setStatus(Status.EN_ATTENTE_VALIDATION);
                System.out.println("Absence " + absence.getId() + " : EN ATTENTE");
            } else {
                // Sinon on rejette l'absence
                absence.setStatus(Status.REJETEE);
                System.out.println("Absence " + absence.getId() + " : REJETEE");
            }
            // On sauvegarde l'absence
            absenceRepository.save(absence);

        }
        System.out.println("Les absences ont été traitées");

        // Chercher les RTT employeurs en statut INITIALE
        List<SpecificAbsence> specificAbsences = specificAbsenceService.findInitialEmployerWtr();
        System.out.println("Traitement de " + specificAbsences.size() + " RTT employeurs");

        // Pour chaque RTT employeur
        for (SpecificAbsence specificAbsence : specificAbsences) {
            System.out.println("Traitement de la RTT employeur : " + specificAbsence.getId());

            // On met la RTT employeur en validée
            specificAbsence.setStatus(Status.VALIDEE);
            System.out.println("RTT employeur " + specificAbsence.getId() + " : VALIDE");

            specificAbsenceService.save(specificAbsence);
        }
        System.out.println("Les RTT employeur ont été traitées");
    }

    /**
     * Méthode permettant de vérifier si l'utilisateur a assez de jours
     * 
     * @param absence l'absence
     * @return true si l'utilisateur a assez de jours, false sinon
     */
    private boolean aAssezDeJours(Absence absence) {
        if (absence.getType() == AbsenceType.UNPAID_LEAVE) {
            return true;
        }
        LocalDate datededebut = absence.getDtDebut().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate datedefin = absence.getDt_fin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        final long requestedDays = Period.between(datededebut, datedefin)
                .getDays() + 1;

        if (absence.getType() == AbsenceType.PAID_LEAVE) {
            return requestedDays < absenceService.remainingPaidLeaves(absence.getUser());
        }

        if (absence.getType() == AbsenceType.RTT_EMPLOYEE) {
            return requestedDays < absenceService.remainingEmployeeWtr(absence.getUser());
        }

        return true;
    }
}
