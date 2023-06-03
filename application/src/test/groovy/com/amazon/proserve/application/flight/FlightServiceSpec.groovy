package com.amazon.proserve.application.flight

import com.amazon.proserve.application.flight.command.ChangeFlightNameCommand
import com.amazon.proserve.application.flight.service.ChangeFlightNameService
import com.amazon.proserve.application.flight.usecase.ChangeFlightNameUseCase
import com.amazon.proserve.domain.flight.Flight
import com.amazon.proserve.domain.flight.repository.FlightRepository
import com.amazon.proserve.domain.flight.vo.*
import spock.lang.Specification

class FlightServiceSpec extends Specification {
    def "change name test"() {
        given:
        Flight flight = Flight.builder()
                .flightNo(FlightNo.of(1L))
                .profileId(ProfileId.of("PRF-1"))
                .flightName(FlightName.of("Bingo"))
                .platform(Platform.ANDROID)
                .build()
        FlightRepository repository = Mock()
        repository.findByFlightNo(FlightNo.of(1L)) >> flight

        ChangeFlightNameUseCase useCase = new ChangeFlightNameService(repository)
        ChangeFlightNameCommand command = ChangeFlightNameCommand.of(1L, "Sally")

        when:
        useCase.changeFlightName(command)

        then:
        1 * repository.save(flight)
        flight.getFlightName().value == "Sally"
    }
}
