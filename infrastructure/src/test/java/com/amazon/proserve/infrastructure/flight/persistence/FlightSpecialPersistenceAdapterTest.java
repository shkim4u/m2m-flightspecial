package com.amazon.proserve.infrastructure.flight.persistence;

import com.amazon.proserve.domain.flight.repository.FlightSpecialRepository;
import com.amazon.proserve.infrastructure.flight.persistence.jpa.FlightSpecialJpaRepository;

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
public class FlightSpecialPersistenceAdapterTest {
    @Autowired
    FlightSpecialJpaRepository repository;

    @Test
    public void testFindAll() {
        FlightSpecialRepository flightSpecialRepository = new FlightSpecialPersistenceAdapter(repository);
        flightSpecialRepository.findAll();
    }
}
