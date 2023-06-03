package com.amazon.proserve.application.flight.service;

import com.amazon.proserve.application.flight.command.ChangeFlightNameCommand;
import com.amazon.proserve.application.flight.usecase.ChangeFlightNameUseCase;
import com.amazon.proserve.domain.flight.Flight;
import com.amazon.proserve.domain.flight.repository.FlightRepository;
import com.amazon.proserve.domain.flight.vo.FlightNo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeFlightNameService implements ChangeFlightNameUseCase {
    private final FlightRepository repository;

    @Override
    public void changeFlightName(ChangeFlightNameCommand command) {
        Flight flight = repository.findByFlightNo(FlightNo.of(command.getFlightNo()));
        flight.changeFlightName(command.getNewFlightName());
        repository.save(flight);
    }
}
