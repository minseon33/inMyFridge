package com.example.inmyfridge.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("user"),
    ADMIN("admin");

    private final String text;
}
