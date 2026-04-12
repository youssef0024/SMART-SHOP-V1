package com.SMART_SHOP.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre de sécurité qui s'exécute une fois par requête.
 * Il vérifie la validité du token JWT dans le header Authorization.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Vérification de la présence du header Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraction du token et de l'email
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // 3. Si l'email est présent et que l'utilisateur n'est pas encore authentifié dans le contexte
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 4. Validation du token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 5. Mise à jour du contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Simulation locale pour tes logs de test
                System.out.println("✅ Authentification réussie pour : " + userEmail);
            }
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    // Petite méthode main pour simulation locale rapide de la logique de détection du header
    public static void main(String[] args) {
        String mockHeader = "Bearer eyJhbGciOiJIUzI1NiJ9...";
        if (mockHeader.startsWith("Bearer ")) {
            System.out.println("Test simulation : Header détecté avec succès.");
            System.out.println("Token extrait : " + mockHeader.substring(7));
        } else {
            System.out.println("Test simulation : Format de header invalide.");
        }
    }
}