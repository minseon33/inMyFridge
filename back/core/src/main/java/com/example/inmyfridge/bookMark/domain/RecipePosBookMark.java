package com.example.inmyfridge.bookMark.domain;

import com.example.inmyfridge.recipeBoard.domain.RecipePost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class RecipePosBookMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @OneToOne
    private RecipePost recipePost; // 북마크 리스트를 유저가 알고 있을 필요가 있을까..? 아니면 디비에서 찾아서 그냥 쭉 조회해주는건...?
}
