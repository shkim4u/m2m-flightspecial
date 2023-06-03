package com.amazon.proserve.domain.flight;

import com.amazon.proserve.domain.flight.entity.FlightNameHistory;
import com.amazon.proserve.domain.flight.vo.*;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
public final class Flight {
    @NotNull
    private FlightNo flightNo;

    @NotNull
    private ProfileId profileId;

    @NotNull
    private FlightName flightName;

    @NotNull
    @Builder.Default
    private PushingStatus pushingStatus = PushingStatus.NORMAL;

    @NotNull
    @Builder.Default
    private PopingStep popingStep = PopingStep.of(PopingStep.MIN_VALUE);

    @NotNull
    private Platform platform;

    @NotNull
    @Builder.Default
    private List<FlightNameHistory> flightNameHistories = new ArrayList<>();

    @NotNull
    private String registerId;

    @NotNull
    @Builder.Default
    private LocalDateTime registrationDateTime = LocalDateTime.now();

    @NotNull
    @Builder.Default
    private LocalDateTime modifyDateTime = LocalDateTime.now();

    /*
     * 플라이트의 이름을 변경한다.
     * 변경하기 전의 이름은 플라이트 이름 이력 리스트에 추가된다.
     */
    public void changeFlightName(final String newFlightName) {
        FlightName oldName = this.flightName;
        FlightName newName = FlightName.of(newFlightName);
        this.addFlightNameHistory(oldName);
        this.flightName = newName;
    }

    /*
     * 플라이트 이름 이력 리스트에 추가하기. 이름을 바꿀 때에만 호출되며 직접 호출해서는 안된다.
     * FlightHistoryNo는 1부터 시작하며 추가될 때마다 1씩 늘어난다.
     */
    private void addFlightNameHistory(FlightName oldName) {
        int currentHistoryCount = this.flightNameHistories.size();
        FlightNameHistoryNo newHistoryNo = FlightNameHistoryNo.of(Long.valueOf(currentHistoryCount + 1));
        FlightNameHistory history = FlightNameHistory.of(
                this.flightNo,
                newHistoryNo,
                oldName);
        flightNameHistories.add(history);
    }
}