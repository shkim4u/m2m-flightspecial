package com.amazon.proserve.domain.flight.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Platform {
    TV("TV", "TV"),
    IOS("IOS", "IOS"),
    ANDROID("ANDROID", "ANDROID");

    @Getter
    private final String code;
    @Getter
    private final String value;
}
