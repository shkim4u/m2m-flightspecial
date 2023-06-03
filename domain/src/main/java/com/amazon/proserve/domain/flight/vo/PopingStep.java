package com.amazon.proserve.domain.flight.vo;

import lombok.Getter;
import lombok.Value;

@Value(staticConstructor = "of")
@Getter
public final class PopingStep {
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 5;
    private final Integer value;

    private PopingStep(final Integer popingStep) {
        if (popingStep < MIN_VALUE || popingStep > MAX_VALUE) {
            throw new IllegalArgumentException("Failed requirements: PopingStep");
        }
        this.value = popingStep;
    }
}
