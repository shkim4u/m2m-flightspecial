package com.amazon.proserve.infrastructure.flight.persistence;

import com.amazon.proserve.domain.flight.FlightSpecial;
import com.amazon.proserve.domain.flight.repository.FlightSpecialRepository;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightSpecialJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlightSpecialPersistenceAdapter implements FlightSpecialRepository {
    private final FlightSpecialJpaRepository repository;

    @Override
    public List<FlightSpecial> findAll() {
        return repository.findAll().stream().map(x -> x.toDomainEntity()).collect(Collectors.toList());
    }
}
