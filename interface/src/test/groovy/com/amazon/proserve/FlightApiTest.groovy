package com.amazon.proserve

import com.amazon.proserve.flight.api.dto.ApiResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import spock.lang.Specification
import com.amazon.proserve.application.flight.view.FlightSpecialView
import java.util.List

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerApiTest extends Specification {
    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    def "get flight special"() {
        given:
        when:
        def entity = this.restTemplate.getForEntity("/flightspecials", List<FlightSpecialView>)

        then:
        entity.getStatusCode() == HttpStatus.OK
    }
}
