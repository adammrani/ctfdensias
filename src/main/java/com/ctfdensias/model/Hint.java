package com.ctfdensias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "hints")
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Hidden from normal responses — revealed only via dedicated endpoint */
    @JsonIgnore
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** Point cost to unlock (0 = free) */
    @Column(nullable = false)
    private Integer cost = 0;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public Hint() {}

    public String reveal() { return this.content; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }
    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}
