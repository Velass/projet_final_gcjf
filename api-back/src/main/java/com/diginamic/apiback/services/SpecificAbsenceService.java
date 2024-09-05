package com.diginamic.apiback.services;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.diginamic.apiback.models.Organization;
import com.diginamic.apiback.models.SpecificAbsence;
import com.diginamic.apiback.repository.SpecificAbsenceRepository;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SpecificAbsenceService {

    @Autowired
    SpecificAbsenceRepository specificAbsenceRepository;

    @Autowired
    OrganizationService organizationService;

    /**
     * Trouve toutes les absences spécifiques.
     *
     * @return une liste d'objets SpecificAbsence
     */
    public List<SpecificAbsence> findAll() {
        return specificAbsenceRepository.findAll();
    }

    /**
     * Trouve une absence spécifique par son id.
     *
     * @param id l'id de l'absence spécifique
     * @return un Optional de l'objet SpecificAbsence
     */
    public Optional<SpecificAbsence> findById(@NonNull Long id) {
        return specificAbsenceRepository.findById(id);
    }

    /**
     * Met à jour une absence spécifique.
     *
     * @param specificAbsence l'absence spécifique à mettre à jour
     * @param id              l'ID de l'absence spécifique à mettre à jour
     * @return l'absence spécifique mise à jour
     * @throws EntityNotFoundException si l'absence spécifique n'est pas trouvée
     */
    public SpecificAbsence updateSpecificAbsence(@Valid @NonNull SpecificAbsence specificAbsence, @NonNull Long id) {
        boolean idAbsence = specificAbsenceRepository.existsById(id);
        if (idAbsence != true) {
            throw new EntityNotFoundException("cette absence spécifique n'existe pas");
        }
        specificAbsence.setId(id);
        specificAbsence.setOrganization(organizationService.findById(specificAbsence.getOrganization_id()).get());
        return specificAbsenceRepository.save(specificAbsence);
    }

    /**
     * Crée une absence spécifique.
     *
     * @param specificAbsence l'absence spécifique à créer
     * @return l'absence spécifique créée
     * @throws specificAbsence
     */
    public SpecificAbsence createSpecificAbsence(Long organiation_id, @Valid SpecificAbsence specificAbsence) {
        Organization organization = organizationService.findById(organiation_id).get();
        specificAbsence.setOrganization(organization);
        return specificAbsenceRepository.save(specificAbsence);
    }

    /**
     * Supprime une absence spécifique.
     *
     * @param id l'ID de l'absence spécifique à supprimer
     * @return l'absence spécifique supprimée
     * @throws EntityNotFoundException si l'absence spécifique n'est pas trouvée
     */
    public SpecificAbsence deleteSpecificAbsence(@NonNull Long id) {
        SpecificAbsence absenceToDelete = specificAbsenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID : " + id + " introuvable"));
        if (absenceToDelete != null) {
            specificAbsenceRepository.deleteById(id);
        }
        return absenceToDelete;
    }

    /**
     * Trouve les absences spécifiques par rapport à l'organisation, le mois et
     * l'année
     * 
     * @param organizationId l'ID de l'organisation
     * @param month          le mois
     * @param year           l'année
     * @return la liste des absences
     */
    public List<SpecificAbsence> findAbsencesAndMonthAndYear(Long organizationId, String month, String year) {
        return specificAbsenceRepository.findAbsencesAndMonthAndYear(organizationId, month, year);
    }

    /**
     * Trouve les RTT employeur en statut initiale
     * 
     * @return la liste des absences
     */
    public List<SpecificAbsence> findInitialEmployerWtr() {
        return specificAbsenceRepository.findInitialEmployerWtr();
    }

    /**
     * Sauvegarde une absence spécifique
     * 
     * @param specificAbsence l'absence spécifique à sauvegarder
     * @return l'absence spécifique sauvegardée
     */
    public SpecificAbsence save(SpecificAbsence specificAbsence) {
        return specificAbsenceRepository.save(specificAbsence);
    }

}
