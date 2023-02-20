package energy.leap.meterhub.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class HourlyReading {

    public HourlyReading (String meterId, Long hourStartEpochAsSec, BigDecimal pricePerKwh, Long readingAsWh) {
        this.id = new HourlyReadingKey(meterId, hourStartEpochAsSec);
        this.pricePerKwh = pricePerKwh;
        this.readingAsWh = readingAsWh;
    }

    @EmbeddedId
    private HourlyReadingKey id;
    private BigDecimal pricePerKwh;
    private Long readingAsWh;
}
