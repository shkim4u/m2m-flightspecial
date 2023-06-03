package com.amazon.proserve.application.flight.view;

import java.time.LocalDateTime;

import com.amazon.proserve.domain.flight.FlightSpecial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlightSpecialView {
    private String header;
    private String body;
    private String origin;
    private String originCode;
    private String destination;
    private String destinationCode;
    private Integer cost;
    private LocalDateTime expiryDate;

    public static FlightSpecialView of(FlightSpecial flightSpecial) {
        return FlightSpecialView.builder()
                .header(flightSpecial.getHeader().getValue())
                .body(flightSpecial.getBody().getValue())
                .origin(flightSpecial.getOrigin().getValue())
                .originCode(flightSpecial.getDestinationCode().getValue())
                .destination(flightSpecial.getDestination().getValue())
                .destinationCode(flightSpecial.getDestinationCode().getValue())
                .cost(flightSpecial.getCost().getValue())
                .expiryDate(flightSpecial.getExpiryDate())
                .build();
    }
}
