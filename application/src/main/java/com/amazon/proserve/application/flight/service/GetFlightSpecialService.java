package com.amazon.proserve.application.flight.service;

import com.amazon.proserve.application.flight.usecase.GetFlightSpecialUseCase;
import com.amazon.proserve.application.flight.view.FlightSpecialView;
import com.amazon.proserve.domain.flight.FlightSpecial;
import com.amazon.proserve.domain.flight.repository.FlightSpecialRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFlightSpecialService implements GetFlightSpecialUseCase {
    private final FlightSpecialRepository repository;

    @Override
    public List<FlightSpecialView> getFlightSpecial() {
        List<FlightSpecial> list = repository.findAll();
        return list.stream().map(x -> FlightSpecialView.of(x)).collect(Collectors.toList());
    }
}
