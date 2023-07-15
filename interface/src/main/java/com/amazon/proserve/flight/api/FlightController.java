package com.amazon.proserve.flight.api;

import com.amazon.proserve.application.flight.command.UpdateFlightSpecialHeaderCommand;
import com.amazon.proserve.application.flight.usecase.GetFlightSpecialUseCase;
import com.amazon.proserve.application.flight.command.UpdateFlightNameCommand;
import com.amazon.proserve.application.flight.usecase.UpdateFlightNameUseCase;
import com.amazon.proserve.application.flight.usecase.UpdateFlightSpecialHeaderUseCase;
import com.amazon.proserve.application.flight.view.FlightSpecialView;
import com.amazon.proserve.flight.api.dto.ApiResult;
import com.amazon.proserve.flight.api.dto.UpdateFlightNameRequest;

import com.amazon.proserve.flight.api.dto.UpdateFlightSpecialHeaderRequest;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/flightspecials")
@RequiredArgsConstructor
public class FlightController {
        private final GetFlightSpecialUseCase getFlightSpecialUseCase;
        private final UpdateFlightNameUseCase updateFlightNameUseCase;
        private final UpdateFlightSpecialHeaderUseCase updateFlightSpecialHeaderUserCase;

        @GetMapping({ "/" })
        public List<FlightSpecialView> getFlightSpecial() {
                return getFlightSpecialUseCase.getFlightSpecial();
        }

        @PostMapping(value = "/{prfId}/name", produces = "application/json")
        public ResponseEntity<ApiResult<String>> updateFlightName(
                @PathVariable(name = "prfId") String profileId,
                @RequestBody UpdateFlightNameRequest request
        ) {
                UpdateFlightNameCommand command = UpdateFlightNameCommand.of(request.getFlightNo(),
                        request.getFlightName());
                updateFlightNameUseCase.updateFlightName(command);
                ApiResult<String> result = ApiResult.<String>builder()
                        .resultCode("Y")
                        .data("The flight name successfully updated")
                        .build();
                return new ResponseEntity<>(result, HttpStatus.OK);
        }

        @PostMapping(value = "/{id}/header", produces = "application/json")
        public ResponseEntity<ApiResult<String>> updateFlightSpecialsHeader(
                @PathVariable(name = "id") String id,
                @RequestBody UpdateFlightSpecialHeaderRequest request
        ) {
                UpdateFlightSpecialHeaderCommand command = UpdateFlightSpecialHeaderCommand.of(request.getId(),
                        request.getFlightSpecialHeader());
                updateFlightSpecialHeaderUserCase.updateFlightSpecialHeader(command);
                ApiResult<String> result = ApiResult.<String>builder()
                        .resultCode("Y")
                        .data("The flight special header successfully updated")
                        .build();
                return new ResponseEntity<>(result, HttpStatus.OK);
        }
}
