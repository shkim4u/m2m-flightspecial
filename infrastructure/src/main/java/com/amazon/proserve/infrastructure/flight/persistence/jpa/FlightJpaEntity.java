package com.amazon.proserve.infrastructure.flight.persistence.jpa;

import com.amazon.proserve.domain.flight.Flight;
import com.amazon.proserve.domain.flight.vo.*;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "flight")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FlightJpaEntity {
        @Id
        @Column(name = "flight_no", nullable = false)
        private Long flightNo;

        @Column(name = "profile_id", nullable = false)
        private String profileId;

        @Column(name = "flight_name", nullable = false)
        private String flightName;

        @Column(name = "pushing_status_code", nullable = false)
        private String pushingStatusCode;

        @Column(name = "poping_step", nullable = false)
        private Integer popingStep;

        @Column(name = "registration_date_time", nullable = false, columnDefinition = "TIMESTAMP")
        private LocalDateTime registrationDateTime;

        @Column(name = "register_id")
        private String registerId;

        @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @JoinColumn(name = "flight_no")
        private List<FlightNameHistoryJpaEntity> flightNameHistories;

        public static FlightJpaEntity fromDomainEntity(Flight domainEntity) {
                return FlightJpaEntity.builder()
                                .flightNo(domainEntity.getFlightNo().getValue())
                                .profileId(domainEntity.getProfileId().getValue())
                                .flightName(domainEntity.getFlightName().getValue())
                                .pushingStatusCode(domainEntity.getPushingStatus().name())
                                .popingStep(domainEntity.getPopingStep().getValue())
                                .registerId(domainEntity.getRegisterId())
                                .registrationDateTime(domainEntity.getRegistrationDateTime())
                                .flightNameHistories(domainEntity.getFlightNameHistories().stream()
                                                .map(FlightNameHistoryJpaEntity::fromDomainEntity)
                                                .collect(Collectors.toList()))
                                .build();
        }

        public Flight toDomainEntity() {
                return Flight.builder()
                                .flightNo(FlightNo.of(this.flightNo))
                                .profileId(ProfileId.of(this.profileId))
                                .flightName(FlightName.of(this.flightName))
                                .pushingStatus(PushingStatus.valueOf(this.pushingStatusCode))
                                .popingStep(PopingStep.of(this.popingStep))
                                .registerId(this.registerId)
                                .registrationDateTime(this.registrationDateTime)
                                .flightNameHistories(this.flightNameHistories.stream()
                                                .map(FlightNameHistoryJpaEntity::toDomainEntity)
                                                .collect(Collectors.toList()))
                                .build();
        }
}
