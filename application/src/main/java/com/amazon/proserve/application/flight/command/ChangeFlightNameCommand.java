package com.amazon.proserve.application.flight.command;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Value(staticConstructor = "of")
public class ChangeFlightNameCommand {
    @NotNull
    @PositiveOrZero
    Long flightNo;
    @NotBlank
    String newFlightName;
}
