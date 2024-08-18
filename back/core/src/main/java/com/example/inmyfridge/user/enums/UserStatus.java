package com.example.inmyfridge.user.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활성화_유저"),
    INACTIVE("비활성화_유저"),
    DELETED("탈퇴한_유저");

    private final String text;

}
