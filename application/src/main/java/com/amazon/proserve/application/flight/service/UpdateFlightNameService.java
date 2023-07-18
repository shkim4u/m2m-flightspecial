package com.amazon.proserve.application.flight.service;

import com.amazon.proserve.application.flight.command.UpdateFlightNameCommand;
import com.amazon.proserve.application.flight.usecase.UpdateFlightNameUseCase;
import com.amazon.proserve.domain.flight.Flight;
import com.amazon.proserve.domain.flight.repository.FlightRepository;
import com.amazon.proserve.domain.flight.vo.FlightNo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateFlightNameService implements UpdateFlightNameUseCase {
    private final FlightRepository repository;

    @Override
    public void updateFlightName(UpdateFlightNameCommand command) {
        Flight flight = repository.findByFlightNo(FlightNo.of(command.getFlightNo()));
        flight.updateFlightName(command.getNewFlightName());
        repository.save(flight);
    }
}
