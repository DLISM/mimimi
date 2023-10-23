package com.example.mimimimetr.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pairs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cat_one_id", nullable = false)
    private Cat catOne;

    @ManyToOne
    @JoinColumn(name = "cat_two_id", nullable = false)
    private Cat catTwo;

}