package com.amazon.proserve.application.flight

import com.amazon.proserve.application.flight.service.GetFlightSpecialService
import com.amazon.proserve.application.flight.usecase.GetFlightSpecialUseCase
import com.amazon.proserve.domain.flight.FlightSpecial
import com.amazon.proserve.domain.flight.repository.FlightSpecialRepository
import com.amazon.proserve.domain.flight.vo.*
import spock.lang.Specification

class FlightSpecialServiceSpec extends Specification {
    // def "change name test"() {
    //     given:
    //     List<FlightSpecial> list = null
    //     FlightSpecialRepository repository = Mock()
    //     repository.findAll() >> list

    //     GetFlightSpecialUseCase useCase = new GetFlightSpecialService(repository)

    //     when:
    //     List<FlightSpecial> sample = useCase.findAll()

    //     then:
    //     sample == list
    // }
}
