package com.amazon.proserve.application.flight.service.outboundservice;

import com.amazon.proserve.domain.flight.vo.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightSpecialHeaderUpdatePublisher {

    private final StreamBridge streamBridge;

    public void publish(Id id) {
        FlightSpecialHeaderUpdateModel model = new FlightSpecialHeaderUpdateModel(id, "비행 정보가 변경되었습니다.");
        Message<FlightSpecialHeaderUpdateModel> message = MessageBuilder.withPayload(model).build();
        streamBridge.send("flightspecial-update-header", message);
    }
}
