package energy.leap.meterhub.service.impl.mapper;

import energy.leap.meterhub.data.entity.HourlyReading;
import energy.leap.meterhub.service.dto.HourlyReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HourlyReadingMapper {
    @Mapping(target="hourStartEpochAsSec", source="hourlyReading.id.hourStartEpochAsSec")
    @Mapping(target="readingAsWh", source="hourlyReading.readingAsWh")
    @Mapping(target="cost", expression="java( hourlyReading.getPricePerKwh().multiply(BigDecimal.valueOf(hourlyReading.getReadingAsWh())).divide(BigDecimal.valueOf(1000)) )")
    HourlyReportDto mapToHourlyReportDto(HourlyReading hourlyReading);
}