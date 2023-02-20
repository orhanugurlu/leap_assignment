package energy.leap.meterhub.service.impl.parser;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class XmlMeterBatchReading {
    private String id;
    private String title;
    private BigDecimal pricePerKwh;
    private XmlReadingUnit readingUnit;
    private List<XmlIntervalReading> intervalReadings = new ArrayList<>();
}
