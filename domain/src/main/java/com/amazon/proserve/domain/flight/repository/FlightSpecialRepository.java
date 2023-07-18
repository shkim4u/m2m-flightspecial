package com.amazon.proserve.domain.flight.repository;

import java.util.List;

import com.amazon.proserve.domain.flight.FlightSpecial;
import com.amazon.proserve.domain.flight.vo.Id;

public interface FlightSpecialRepository {
    List<FlightSpecial> findAll();

    FlightSpecial findById(Id id);

    Id save(FlightSpecial flightSpecial);
}
