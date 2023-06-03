package com.amazon.proserve.infrastructure.flight.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightSpecialJpaRepository extends JpaRepository<FlightSpecialJpaEntity, Long> {
}
