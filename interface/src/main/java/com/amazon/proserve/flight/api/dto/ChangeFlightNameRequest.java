package com.amazon.proserve.flight.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ChangeFlightNameRequest {
    private Long flightNo;
    private String flightName;
}
