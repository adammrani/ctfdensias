package main.java.com.ctfdensias.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "flags")
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String hash; // Le texte ou le regex du drapeau

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Flag() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}