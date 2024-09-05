package com.diginamic.apiback.services;

import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.diginamic.apiback.dto.UserDTO;
import com.diginamic.apiback.models.User;
import com.diginamic.apiback.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Charge un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur de l'utilisateur
     * @return l'objet utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findByEmail(username);

        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("Invalid credentials.");
    }

    public User save(final User user) {
        return userRepository.save(user);
    }

    /**
     * Trouve tous les utilisateurs.
     *
     * @return une liste d'objets UserDTO
     */
    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(user.toDto());
        }

        return userDTOs;
    }

    /**
     * Trouve un utilisateur par son id.
     *
     * @param id l'id de l'utilisateur
     * @return un Optional de l'objet User
     */
    public Optional<User> findById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    /**
     * Trouve les utilisateurs par service.
     *
     * @param service le service des utilisateurs
     * @return une liste d'objets User
     */
    public List<User> findByService(com.diginamic.apiback.models.Service service) {
        return userRepository.findByService(service);
    }

    /**
     * Met à jour un utilisateur.
     *
     * @param user l'utilisateur à mettre à jour
     * @param id   l'ID de l'utilisateur à mettre à jour
     * @return l'utilisateur mis à jour
     * @throws EntityNotFoundException si l'utilisateur n'est pas trouvé
     */
    public User updateUser(@Valid @NonNull User user, @NonNull Long id) {
        boolean idUser = userRepository.existsById(id);
        if (idUser != true) {
            throw new EntityNotFoundException("cet user n'existe pas");
        }
        return userRepository.save(user);
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id l'ID de l'utilisateur à supprimer
     * @return l'utilisateur supprimé
     * @throws EntityNotFoundException si l'utilisateur n'est pas trouvé
     */
    public User deleteUser(@NonNull Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID : " + id + " introuvable"));
        if (userToDelete != null) {
            userRepository.deleteById(id);
        }
        return userToDelete;
    }

    /**
     * Authentifie un utilisateur.
     *
     * @param email      l'email de l'utilisateur
     * @param motDePasse le mot de passe de l'utilisateur
     * @return l'utilisateur authentifié, ou null si l'authentification échoue
     */
    public User authenticateUser(String email, String motDePasse) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            String encodedPassword = user.getPassword();
            if (passwordEncoder.matches(motDePasse, encodedPassword)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Trouve un utilisateur par son email.
     *
     * @param email l'email de l'utilisateur
     * @return un Optional de l'objet User
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
