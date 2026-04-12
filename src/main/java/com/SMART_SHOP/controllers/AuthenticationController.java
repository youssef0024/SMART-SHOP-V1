package com.SMART_SHOP.controllers;

import com.SMART_SHOP.security.AuthenticationRequest; // À créer si absent
import com.SMART_SHOP.security.AuthenticationResponse; // À créer si absent
import com.SMART_SHOP.security.AuthenticationService; // À créer si absent
import com.SMART_SHOP.security.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    // Étape 1 : Vérification initiale et envoi du code par mail
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        // Appelle la méthode qui génère l'OTP et l'envoie via Mailtrap
        return ResponseEntity.ok(service.preAuthenticate(request));
    }

    // Étape 2 : Vérification du code reçu sur Mailtrap
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthenticationResponse> verifyOtp(
            @RequestParam String email,
            @RequestParam String code
    ) {
        // Renvoie enfin le JWT si le code est correct
        return ResponseEntity.ok(service.verifyOtpAndGenerateToken(email, code));
    }

}