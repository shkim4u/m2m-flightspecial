package com.amazon.proserve.application.flight.usecase;

import com.amazon.proserve.application.flight.command.ChangeFlightNameCommand;

public interface ChangeFlightNameUseCase {
    void changeFlightName(ChangeFlightNameCommand command);
}
