package com.amazon.proserve.infrastructure.flight.persistence;

import com.amazon.proserve.domain.flight.FlightSpecial;
import com.amazon.proserve.domain.flight.repository.FlightSpecialRepository;
import com.amazon.proserve.domain.flight.vo.Header;
import com.amazon.proserve.domain.flight.vo.Id;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightSpecialJpaEntity;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightSpecialJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
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

    @Override
    public FlightSpecial findById(Id id) {
        return repository.findById(id.getValue())
                .orElseThrow(NoResultException::new)
                .toDomainEntity();
    }

    @Override
    public Id save(FlightSpecial flightSpecial) {
        FlightSpecialJpaEntity entity = FlightSpecialJpaEntity.fromDomainEntity(flightSpecial);
        FlightSpecialJpaEntity savedEntity = repository.saveAndFlush(entity);
        return Id.of(savedEntity.getId());
    }
}
