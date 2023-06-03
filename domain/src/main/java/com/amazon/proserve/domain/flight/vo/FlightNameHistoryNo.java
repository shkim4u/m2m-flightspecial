package com.amazon.proserve.domain.flight.vo;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value(staticConstructor = "of")
public class FlightNameHistoryNo {
    @NotNull
    Long value;
}
