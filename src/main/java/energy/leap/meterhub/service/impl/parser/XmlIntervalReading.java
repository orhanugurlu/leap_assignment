package energy.leap.meterhub.service.impl.parser;

import lombok.*;
import org.threeten.extra.Interval;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class XmlIntervalReading {
    private Long startEpochAsSec;
    private Long durationAsSec;
    private Long reading;

    public Interval getInterval() {
        return Interval.of(Instant.ofEpochSecond(startEpochAsSec), Duration.ofSeconds(durationAsSec));
    }

    public boolean overlaps(XmlIntervalReading other) {
        return this.getInterval().overlaps(other.getInterval());
    }

}
