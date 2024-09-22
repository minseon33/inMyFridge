package com.example.inmyfridge.recipeBoard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stepTotalCount; // steps의 총 개수

    @OneToMany
    private List<RecipeStep> recipeSteps;
}
