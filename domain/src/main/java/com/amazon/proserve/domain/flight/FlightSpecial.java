package com.amazon.proserve.domain.flight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.amazon.proserve.domain.flight.vo.Body;
import com.amazon.proserve.domain.flight.vo.Cost;
import com.amazon.proserve.domain.flight.vo.Destination;
import com.amazon.proserve.domain.flight.vo.DestinationCode;
import com.amazon.proserve.domain.flight.vo.Header;
import com.amazon.proserve.domain.flight.vo.Id;
import com.amazon.proserve.domain.flight.vo.Origin;
import com.amazon.proserve.domain.flight.vo.OriginCode;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class FlightSpecial {
    @NotNull
    private Id id;

    @NotNull
    private Header header;

    private Body body;

    private Origin origin;

    private OriginCode originCode;

    private Destination destination;

    private DestinationCode destinationCode;

    private Cost cost;

    private LocalDateTime expiryDate;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlightSpecial) {
            FlightSpecial entity = (FlightSpecial) obj;
            if (header.equals(entity.getHeader())
                    && body.equals(entity.getBody())
                    && origin.equals(entity.getOrigin())
                    && originCode.equals(entity.getOriginCode())
                    && destination.equals(entity.getDestination())
                    && destinationCode.equals(entity.getDestinationCode())
                    && cost.equals(entity.getCost())
                    && expiryDate.equals(entity.getExpiryDate()))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, body, origin, originCode, destination, destinationCode, cost, expiryDate);
    }
}
