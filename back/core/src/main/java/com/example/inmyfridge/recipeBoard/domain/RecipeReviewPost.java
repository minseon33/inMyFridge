package com.example.inmyfridge.recipeBoard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class RecipeReviewPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long recipePostId;

    private String title;

    private String contenrs;

    @OneToMany
    private List<ReviewImage> reviewImages;

    @OneToMany
    private List<Like> like;

}
