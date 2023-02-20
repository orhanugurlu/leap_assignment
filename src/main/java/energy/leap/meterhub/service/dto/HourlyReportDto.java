package energy.leap.meterhub.service.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class HourlyReportDto {
    Long hourStartEpochAsSec;
    Long readingAsWh;
    BigDecimal cost;
}
