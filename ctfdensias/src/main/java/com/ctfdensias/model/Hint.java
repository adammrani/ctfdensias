package main.java.com.ctfdensias.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "hints")
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Hint() {
    }

    public String reveal() {
        return this.content;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}