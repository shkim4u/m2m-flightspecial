package com.amazon.proserve.domain.flight.vo;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value(staticConstructor = "of")
public class FlightName {
    private static final int FLIGHT_NAME_MAX_LENGTH = 30;
    @NotNull
    String value;

    private FlightName(final String flightName) {
        if (isNotValidate(flightName)) {
            throw new IllegalArgumentException("Failed requirements: flightName");
        }
        this.value = flightName;
    }

    private boolean isNotValidate(String flightName) {
        return flightName == null ||
                flightName.isEmpty() ||
                flightName.length() > FLIGHT_NAME_MAX_LENGTH;
    }
}
