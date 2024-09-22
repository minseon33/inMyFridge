package com.example.inmyfridge.hashTag.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TagType {

    /**
     * 요리재료 카테고리를 타입으로 두자.
     */
    MEAT("Meat"), //육류
    VAGERABLES("Vegetables"), // 채소
    FRUITS("Fruits"), //과일
    FISH("Fish"), //어류
    DAIRY_PRODUCTS("DairyProducts"),//유제품
    BEVERAGES("Beverages"),//음료
    SIDE_DISHES("SideDishes"), //반찬
    SAUCES("Sauces"); // 소스

    private final String text;
}
