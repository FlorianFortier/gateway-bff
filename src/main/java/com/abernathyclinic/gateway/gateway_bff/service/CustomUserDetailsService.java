package com.abernathyclinic.gateway.gateway_bff.service;

import com.abernathyclinic.gateway.gateway_bff.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service personnalisé pour la gestion des détails utilisateur dans le contexte de la sécurité.
 * <p>
 * Cette classe implémente l'interface {@link UserDetailsService} pour charger les détails d'un utilisateur
 * à partir de la base de données ou d'une autre source de données en utilisant un {@code UserRepository}.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructeur pour injecter le dépôt utilisateur.
     *
     * @param userRepository Le dépôt utilisateur pour accéder aux données utilisateur.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge les détails de l'utilisateur en fonction de son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'utilisateur à charger.
     * @return Les détails de l'utilisateur sous la forme d'un {@link UserDetails}.
     * @throws UsernameNotFoundException Si aucun utilisateur correspondant n'est trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
