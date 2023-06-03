package com.amazon.proserve.application.flight.usecase;

import java.util.List;

import com.amazon.proserve.application.flight.view.FlightSpecialView;

public interface GetFlightSpecialUseCase {
    List<FlightSpecialView> getFlightSpecial();
}
