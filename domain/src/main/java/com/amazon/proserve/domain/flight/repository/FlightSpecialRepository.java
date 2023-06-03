package com.amazon.proserve.domain.flight.repository;

import java.util.List;

import com.amazon.proserve.domain.flight.FlightSpecial;

public interface FlightSpecialRepository {
    List<FlightSpecial> findAll();
}
