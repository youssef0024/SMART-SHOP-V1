package com.SMART_SHOP.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "_users") // "user" est un mot réservé en Postgres
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- AJOUTE CES DEUX LIGNES ICI ---
    private String firstname;
    private String lastname;
    // ----------------------------------
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // Simulation locale pour test rapide
    public static void main(String[] args) {
        User testUser = User.builder().email("test@emsi.ma").role(Role.ADMIN).build();
        System.out.println("Simulation Entity: " + testUser.getEmail() + " as " + testUser.getRole());
    }
}