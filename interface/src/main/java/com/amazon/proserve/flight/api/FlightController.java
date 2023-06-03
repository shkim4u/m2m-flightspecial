package com.amazon.proserve.flight.api;

import com.amazon.proserve.application.flight.usecase.GetFlightSpecialUseCase;
import com.amazon.proserve.application.flight.command.ChangeFlightNameCommand;
import com.amazon.proserve.application.flight.usecase.ChangeFlightNameUseCase;
import com.amazon.proserve.application.flight.view.FlightSpecialView;
import com.amazon.proserve.flight.api.dto.ApiResult;
import com.amazon.proserve.flight.api.dto.ChangeFlightNameRequest;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class FlightController {
        private final GetFlightSpecialUseCase getFlightSpecialUseCase;
        private final ChangeFlightNameUseCase changeFlightNameUseCase;

        @GetMapping({ "/flightspecials" })
        public List<FlightSpecialView> getFlightSpecial() {
                return getFlightSpecialUseCase.getFlightSpecial();
        }

        @PostMapping(value = "/flight/{prfId}/name", produces = "application/json")
        public ResponseEntity<ApiResult<String>> changeFlightName(@PathVariable(name = "prfId") String profileId,
                        @RequestBody ChangeFlightNameRequest request) {
                ChangeFlightNameCommand command = ChangeFlightNameCommand.of(request.getFlightNo(),
                                request.getFlightName());
                changeFlightNameUseCase.changeFlightName(command);
                ApiResult<String> result = ApiResult.<String>builder()
                                .resultCode("Y")
                                .data("Changed the flight name successfully")
                                .build();
                return new ResponseEntity<>(result, HttpStatus.OK);
        }
}
