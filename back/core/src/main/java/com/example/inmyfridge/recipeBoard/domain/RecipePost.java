package com.example.inmyfridge.recipeBoard.domain;

import com.example.inmyfridge.comment.domain.PostComment;
import com.example.inmyfridge.hashTag.domain.CookingTool;
import com.example.inmyfridge.hashTag.domain.Ingredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class RecipePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String title;

    private String contenrs;

    @OneToOne
    private Recipe recipe;

    @OneToMany
    private List<Ingredient> ingredients;

    @OneToMany
    private List<CookingTool> cookingTools;

    @OneToMany
    private List<PostComment> postComments;

    @OneToMany
    private List<Like> like;
    /**
     * todo 24.9.2) (고민) enum값으로 리뷰인지 레시피포스트인지 구분하는 필드만 두고 Like엔티티는 하나만 두는게 좋으려나..
     *  아니면 리뷰좋아요, 포스트좋아요 두개를 따로 두는게 좋으려나… (고민쓰..)
     *  리뷰 좋아요랑 포스트좋아요랑 포스트ID빼고는 필드값이 다 같은데..
     *
     */

}
