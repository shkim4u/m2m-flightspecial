package com.amazon.proserve.application.flight.service.outboundservice;

import com.amazon.proserve.domain.flight.vo.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightSpecialHeaderUpdateModel {

    private Id id;
    private String message;

}
