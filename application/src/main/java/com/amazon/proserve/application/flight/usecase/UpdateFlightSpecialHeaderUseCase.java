package com.amazon.proserve.application.flight.usecase;

import com.amazon.proserve.application.flight.command.UpdateFlightSpecialHeaderCommand;

public interface UpdateFlightSpecialHeaderUseCase {
    void updateFlightSpecialHeader(UpdateFlightSpecialHeaderCommand command);
}
