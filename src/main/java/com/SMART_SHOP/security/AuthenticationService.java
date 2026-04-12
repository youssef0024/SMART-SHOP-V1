package com.SMART_SHOP.security;

import com.SMART_SHOP.entities.User;
import com.SMART_SHOP.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService; // Service pour l'envoi Mailtrap

    // Stockage temporaire des codes (Email -> Code)
    // En production, on utiliserait Redis pour l'expiration automatique
    private final Map<String, String> otpStorage = new HashMap<>();

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(com.SMART_SHOP.entities.Role.USER)
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // ÉTAPE 1 : Vérification des identifiants et envoi du mail
    public String preAuthenticate(AuthenticationRequest request) {
        // 1. Vérifie l'email et le mot de passe via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Si les identifiants sont bons, on génère le code OTP
        String otp = emailService.generateOtp();

        // 3. On sauvegarde le code en mémoire
        otpStorage.put(request.getEmail(), otp);

        // 4. On envoie le mail via Mailtrap
        emailService.sendOtpEmail(request.getEmail(), otp);

        return "OTP_SENT";
    }

    // ÉTAPE 2 : Vérification du code saisi par l'utilisateur et génération du JWT
    public AuthenticationResponse verifyOtpAndGenerateToken(String email, String code) {
        String storedOtp = otpStorage.get(email);

        // Vérification si le code correspond
        if (storedOtp != null && storedOtp.equals(code)) {
            // Supprime le code pour qu'il ne soit plus réutilisable
            otpStorage.remove(email);

            var user = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Génération finale du token
            var jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } else {
            throw new RuntimeException("Code OTP invalide ou expiré");
        }
    }
}