package com.example.mimimimetr.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_pairs_votes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPairsVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pairs_id", nullable = false)
    private CatPair catPair;

    @Column(name = "user_session", nullable = false)
    private String userSession;

    @ManyToOne
    @JoinColumn(name = "cat_id")
    private Cat cat;
}
