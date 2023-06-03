package com.amazon.proserve.domain.flight.entity;

import lombok.Value;

import javax.validation.constraints.NotNull;

import com.amazon.proserve.domain.flight.vo.FlightName;
import com.amazon.proserve.domain.flight.vo.FlightNameHistoryNo;
import com.amazon.proserve.domain.flight.vo.FlightNo;

@Value(staticConstructor = "of")
public class FlightNameHistory {
    @NotNull
    FlightNo flightNo;
    @NotNull
    FlightNameHistoryNo flightNameHistoryNo;
    @NotNull
    FlightName flightName;
}
