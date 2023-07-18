package com.amazon.proserve.application.flight.usecase;

import com.amazon.proserve.application.flight.command.UpdateFlightNameCommand;

public interface UpdateFlightNameUseCase {
    void updateFlightName(UpdateFlightNameCommand command);
}
