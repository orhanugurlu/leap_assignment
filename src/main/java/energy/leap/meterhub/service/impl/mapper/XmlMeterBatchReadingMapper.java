package energy.leap.meterhub.service.impl.mapper;

import energy.leap.meterhub.data.entity.HourlyReading;
import energy.leap.meterhub.service.impl.parser.XmlIntervalReading;
import energy.leap.meterhub.service.impl.parser.XmlMeterBatchReading;
import energy.leap.meterhub.service.impl.parser.XmlReadingUnit;

import java.util.ArrayList;
import java.util.List;

public class XmlMeterBatchReadingMapper {

    private XmlMeterBatchReadingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static List<HourlyReading> convertReadingsToHourlyReadings(XmlMeterBatchReading batchReading) {
        List<HourlyReading> readings = new ArrayList<>();
        for (XmlIntervalReading intervalReading : batchReading.getIntervalReadings()) {
            Long readingAsWh =
                    batchReading.getReadingUnit() == XmlReadingUnit.WH
                            ? intervalReading.getReading()
                            : intervalReading.getReading() * 1000;
            HourlyReading hourlyReading =
                    new HourlyReading(batchReading.getId(),
                            intervalReading.getStartEpochAsSec(),
                            batchReading.getPricePerKwh(),
                            readingAsWh
                    );
            readings.add(hourlyReading);
        }
        return readings;
    }
}
