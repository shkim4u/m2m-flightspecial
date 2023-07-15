package com.amazon.proserve.infrastructure.flight.persistence.jpa;

import lombok.*;

import com.amazon.proserve.domain.flight.FlightSpecial;
import com.amazon.proserve.domain.flight.vo.Body;
import com.amazon.proserve.domain.flight.vo.Cost;
import com.amazon.proserve.domain.flight.vo.Destination;
import com.amazon.proserve.domain.flight.vo.DestinationCode;
import com.amazon.proserve.domain.flight.vo.Header;
import com.amazon.proserve.domain.flight.vo.Origin;
import com.amazon.proserve.domain.flight.vo.OriginCode;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "flightspecial")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FlightSpecialJpaEntity {
        @Id
        @Column(name = "id", nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "header", nullable = false)
        private String header;

        @Column(name = "body", nullable = true)
        private String body;

        @Column(name = "origin", nullable = true)
        private String origin;

        @Column(name = "originCode", nullable = true)
        private String originCode;

        @Column(name = "destination", nullable = true)
        private String destination;

        @Column(name = "destinationCode", nullable = true)
        private String destinationCode;

        @Column(name = "cost", nullable = false)
        private Integer cost;

        @Column(name = "expiryDate", columnDefinition = "DATE")
        private LocalDateTime expiryDate;

        public static FlightSpecialJpaEntity fromDomainEntity(FlightSpecial domainEntity) {
                return FlightSpecialJpaEntity.builder()
                                .id(Long.valueOf(domainEntity.getId().getValue()))
                                .header(domainEntity.getHeader().getValue())
                                .body(domainEntity.getBody() != null ? domainEntity.getBody().getValue() : "")
                                .origin(domainEntity.getOrigin() != null ? domainEntity.getOrigin().getValue() : "")
                                .originCode(domainEntity.getOriginCode() != null
                                                ? domainEntity.getOriginCode().getValue()
                                                : "")
                                .destination(domainEntity.getDestination() != null
                                                ? domainEntity.getDestination().getValue()
                                                : "")
                                .destinationCode(domainEntity.getDestinationCode() != null
                                                ? domainEntity.getDestinationCode().getValue()
                                                : "")
                                .cost(domainEntity.getCost().getValue())
                                .expiryDate(domainEntity.getExpiryDate())
                                .build();
        }

        public FlightSpecial toDomainEntity() {
                return FlightSpecial.builder()
                                .header(Header.of("[" + System.getenv("CODEBUILD_BUILD_NUMBER") + "] ==> " + this.header))
                                .body(Body.of(this.body))
                                .origin(Origin.of(this.origin))
                                .originCode(OriginCode.of(this.originCode))
                                .destination(Destination.of(this.destination))
                                .destinationCode(DestinationCode.of(this.destinationCode))
                                .cost(Cost.of(this.cost))
                                .expiryDate(this.expiryDate)
                                .build();
        }
}
