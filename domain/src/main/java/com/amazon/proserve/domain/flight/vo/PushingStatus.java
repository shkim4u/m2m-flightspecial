package com.amazon.proserve.domain.flight.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PushingStatus {
    DYING("10", "VERYLOW"),
    HUNGRY("20", "LOW"),
    NORMAL("30", "NORMAL"),
    HAPPY("40", "HIGH"),
    FULL("50", "VERYHIGH");

    @Getter
    private final String code;
    @Getter
    private final String value;
}
