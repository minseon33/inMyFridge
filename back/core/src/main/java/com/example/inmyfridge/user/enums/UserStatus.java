package com.example.inmyfridge.user.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("activeUser"),
    INACTIVE("inactiveUser"),
    DELETED("deletedUser");

    private final String text;

}
