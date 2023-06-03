package com.amazon.proserve.infrastructure.flight.persistence;

import com.amazon.proserve.domain.flight.Flight;
import com.amazon.proserve.domain.flight.repository.FlightRepository;
import com.amazon.proserve.domain.flight.vo.*;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;

//@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = TestApplication.class)
@Transactional
public class FlightPersistenceAdapterTest {
    @Autowired
    FlightJpaRepository repository;

    @Test
    public void saveAndGet() {
        FlightRepository FlightRepository = new FlightPersistenceAdapter(repository);
        Flight flight = Flight.builder()
                .flightNo(FlightNo.of(1L))
                .profileId(ProfileId.of("PRF-1"))
                .flightName(FlightName.of("Bingo"))
                .platform(Platform.ANDROID)
                .registerId("TEST-ID-1")
                .build();
        FlightRepository.save(flight);
        FlightRepository.findByFlightNo(flight.getFlightNo());
    }
}
