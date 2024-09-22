package com.example.inmyfridge.hashTag.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@RequiredArgsConstructor
public class IngredientTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    //소비기한
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private TagType tagType;

    // 알람 여부

    // 재료 상태


}
