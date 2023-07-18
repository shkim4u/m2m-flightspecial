package com.amazon.proserve.application.flight

import com.amazon.proserve.application.flight.command.UpdateFlightNameCommand
import com.amazon.proserve.application.flight.service.UpdateFlightNameService
import com.amazon.proserve.application.flight.usecase.UpdateFlightNameUseCase
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

        UpdateFlightNameUseCase useCase = new UpdateFlightNameService(repository)
        UpdateFlightNameCommand command = UpdateFlightNameCommand.of(1L, "Sally")

        when:
        useCase.updateFlightName(command)

        then:
        1 * repository.save(flight)
        flight.getFlightName().value == "Sally"
    }
}
