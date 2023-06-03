package com.amazon.proserve.infrastructure.flight.persistence;

import com.amazon.proserve.domain.flight.Flight;
import com.amazon.proserve.domain.flight.repository.FlightRepository;
import com.amazon.proserve.domain.flight.vo.FlightNo;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightJpaEntity;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;

@Component
@RequiredArgsConstructor
public class FlightPersistenceAdapter implements FlightRepository {
    private final FlightJpaRepository repository;

    @Override
    public FlightNo save(Flight flight) {
        FlightJpaEntity entity = FlightJpaEntity.fromDomainEntity(flight);
        FlightJpaEntity savedEntity = repository.saveAndFlush(entity);
        return FlightNo.of(savedEntity.getFlightNo());
    }

    @Override
    public Flight findByFlightNo(FlightNo flightNo) {
        return repository.findById(flightNo.getValue())
                .orElseThrow(NoResultException::new)
                .toDomainEntity();
    }
}
