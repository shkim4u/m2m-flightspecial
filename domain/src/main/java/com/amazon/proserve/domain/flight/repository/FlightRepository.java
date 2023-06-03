package com.amazon.proserve.domain.flight.repository;

import com.amazon.proserve.domain.flight.Flight;
import com.amazon.proserve.domain.flight.vo.FlightNo;

public interface FlightRepository {
    FlightNo save(Flight flight);

    Flight findByFlightNo(FlightNo flightNo);
}
