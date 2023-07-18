package com.amazon.proserve.domain.flight.vo;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value(staticConstructor = "of")
public class Header {
    private static final int FLIGHTSPECIAL_HEADER_MAX_LENGTH = 255;

    @NotNull
    String value;

    private Header(final String flightSpecialHeader) {
        if (isNotValidate(flightSpecialHeader)) {
            throw new IllegalArgumentException("Failed requirements: flightSpecialHeader");
        }
        this.value = flightSpecialHeader;
    }

    private boolean isNotValidate(String flightSpecialHeader) {
        return flightSpecialHeader == null ||
                flightSpecialHeader.isEmpty() ||
                flightSpecialHeader.length() > FLIGHTSPECIAL_HEADER_MAX_LENGTH;
    }
}
