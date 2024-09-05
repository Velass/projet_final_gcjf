package com.diginamic.apiback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diginamic.apiback.models.Service;
import com.diginamic.apiback.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Méthode permettant de rechercher un utilisateur par son email
     * 
     * @param email l'email
     * @return l'utilisateur
     */
    Optional<User> findByEmail(String email);

    /**
     * Méthode permettant de rechercher un utilisateur par son service
     * 
     * @param service le service
     * @return la liste des utilisateurs
     */
    List<User> findByService(Service service);

}
