package energy.leap.meterhub.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class HourlyReadingKey implements Serializable {
    @Column
    private String meterId;
    // hourId corresponds to start time of an hour and used together with
    // meterId to serve as a unique identifier per hourly reading
    @Column
    private Long hourStartEpochAsSec;
}
