package com.SMART_SHOP.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    // Espace Front Office : Accessible aux clients (USER) et à l'ADMIN
    @GetMapping("/frontoffice/catalogue")
    public ResponseEntity<String> getCatalogue() {
        return ResponseEntity.ok("Bienvenue dans la boutique ! Voici la liste des vêtements et accessoires disponibles.");
    }

    // Espace Back Office : Strictement réservé au propriétaire (ADMIN)
    @GetMapping("/backoffice/stats")
    public ResponseEntity<String> getAdminStats() {
        return ResponseEntity.ok("Tableau de bord gérant : Vous avez 15 nouvelles commandes en attente de livraison (COD).");
    }
}