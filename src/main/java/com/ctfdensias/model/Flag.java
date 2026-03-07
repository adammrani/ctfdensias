package com.ctfdensias.model;

import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Entity
@Table(name = "flags")
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Stored as SHA-256 hex hash — never stored in plaintext */
    @Column(nullable = false)
    private String hash;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public Flag() {}

    // -------------------------------------------------------
    // Hashing utility
    // -------------------------------------------------------

    public static String hashFlag(String rawFlag) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(rawFlag.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /** Set the flag value — automatically hashes it before storing */
    public void setRawFlag(String rawFlag) {
        this.hash = hashFlag(rawFlag);
    }

    /** Check a submitted flag against the stored hash */
    public boolean matches(String submittedFlag) {
        if (submittedFlag == null) return false;
        return this.hash.equals(hashFlag(submittedFlag));
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}
