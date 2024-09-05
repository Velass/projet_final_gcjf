package com.diginamic.apiback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.diginamic.apiback.enums.AbsenceType;
import com.diginamic.apiback.models.Absence;
import com.diginamic.apiback.models.User;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    /**
     * Méthode permettant de rechercher les absences d'un utilisateur
     * 
     * @param user l'utilisateur
     * @return la liste des absences
     */
    List<Absence> findByUserOrderByDtDebutDesc(User user);

    @Query(value = """
            select a from Absence a
            INNER JOIN User u ON a.user_id = u.id
            WHERE u.manager = :user
            ORDER BY a.dtDebut DESC
            """)

    List<Absence> findByManagerId(User user);

    @Query(value = "SELECT * FROM absence WHERE status = :status", nativeQuery = true)
    List<Absence> findByStatus(String status);

    /**
     * Méthode permettant de rechercher les absences d'un utilisateur par mois et
     * année
     * 
     * @param id    l'ID de l'utilisateur
     * @param month le mois
     * @param year  l'année
     * @return la liste des absences
     */
    @Query(value = """
            select a from Absence a
            INNER JOIN User u ON a.user_id = u.id
            WHERE a.user_id = :id
            AND MONTH(a.dtDebut) = :month
            AND YEAR(a.dtDebut) = :year
            AND a.status = VALIDEE
            ORDER BY a.dtDebut ASC
            """)
    List<Absence> findAbsencesAndMonthAndYear(Long id, String month, String year);

    /**
     * Méthode permettant de donner la somme des absences validées d'un utilisateur
     * 
     * @param user l'utilisateur
     * @param type le type d'absence
     * @return la liste des absences
     */
    @Query("""
                SELECT COALESCE(SUM(DATEDIFF(a.dt_fin,a.dtDebut)+1),0)
                FROM Absence a
                WHERE a.status = Status.VALIDEE
                AND a.user = :user
                AND a.type = :type
            """)
    long sumApprovedAbsences(User user, AbsenceType type);

}
