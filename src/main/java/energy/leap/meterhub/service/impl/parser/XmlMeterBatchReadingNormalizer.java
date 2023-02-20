package energy.leap.meterhub.service.impl.parser;

import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingContentException;
import org.threeten.extra.Interval;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

public class XmlMeterBatchReadingNormalizer {

    private Long getHourStart(Long timeSec) {
        return Instant
                .ofEpochSecond(timeSec)
                .atZone(ZoneOffset.UTC)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant()
                .getEpochSecond();
    }

    private boolean isBackToBack(Interval before, Interval after) {
        return before.isBefore(after) && Duration.between(before.getEnd(), after.getStart()) == Duration.ZERO;
    }

    public void checkIfOverlapsOrOverflows(XmlIntervalReading newReading, List<XmlIntervalReading> others) {
        for (XmlIntervalReading reading : others) {
            if (newReading.overlaps(reading)) {
                throw new IllegalMeterBatchReadingContentException(newReading + " overlaps with " + reading);
            }
        }
        Instant nextHourStart = Instant.ofEpochSecond(getHourStart(newReading.getStartEpochAsSec())).plusSeconds(3600);
        Interval nextHourInterval = Interval.of(nextHourStart, Duration.ofSeconds(3600));
        if (newReading.getInterval().overlaps(nextHourInterval)) {
            throw new IllegalMeterBatchReadingContentException(newReading + " overflows hour");
        }
    }

    private Map<Long, List<XmlIntervalReading>> toReadingsByHour(XmlMeterBatchReading meterReading) {
        Map<Long, List<XmlIntervalReading>> readingsByHour = new HashMap<>();
        for (XmlIntervalReading reading : meterReading.getIntervalReadings()) {
            Long hourStart = getHourStart(reading.getStartEpochAsSec());
            List<XmlIntervalReading> hourReadings = readingsByHour.getOrDefault(hourStart, new ArrayList<>());
            checkIfOverlapsOrOverflows(reading, hourReadings);
            hourReadings.add(reading);
            readingsByHour.putIfAbsent(hourStart, hourReadings);
        }
        return readingsByHour;
    }

    private XmlIntervalReading combineReadingsOfHour(Long hourStart, List<XmlIntervalReading> hourReadings) {
        // Sort by time
        List<XmlIntervalReading> hourReadingsSortedByStart = hourReadings.stream()
                .sorted(Comparator.comparingLong(XmlIntervalReading::getStartEpochAsSec))
                .toList();
        // Combine intervals
        Long totalValue = 0L;
        Interval combinedReadingInterval = null;
        for (XmlIntervalReading reading : hourReadingsSortedByStart) {
            if (combinedReadingInterval == null) {
                combinedReadingInterval = reading.getInterval();
                totalValue += reading.getReading();
            } else if (isBackToBack(combinedReadingInterval, reading.getInterval())) {
                combinedReadingInterval = combinedReadingInterval.union(reading.getInterval());
                totalValue += reading.getReading();
            } else {
                throw new IllegalMeterBatchReadingContentException("Disconnected interval exists for start " + hourStart);
            }
        }
        // Check if equal to full hour
        Interval fullHour = Interval.of(Instant.ofEpochSecond(hourStart), Duration.ofSeconds(3600));
        if (!fullHour.equals(combinedReadingInterval)) {
            throw new IllegalMeterBatchReadingContentException("Partial hour for " + hourStart);
        } else {
            return new XmlIntervalReading(hourStart, 3600L, totalValue);
        }
    }

    public XmlMeterBatchReading normalize(XmlMeterBatchReading meterReading) {
        List<XmlIntervalReading> intervalReadings = new ArrayList<>();
        // Group by hour and check for overlaps
        Map<Long, List<XmlIntervalReading>> readingsByHour = toReadingsByHour(meterReading);
        // Combine hour readings and check for completeness
        for (Map.Entry<Long, List<XmlIntervalReading>> entry : readingsByHour.entrySet()) {
            intervalReadings.add(combineReadingsOfHour(entry.getKey(), entry.getValue()));
        }
        // Return new XmlMeterReading with combined/checked readings
        return new XmlMeterBatchReading(meterReading.getId(), meterReading.getTitle(), meterReading.getPricePerKwh(),
                meterReading.getReadingUnit(), intervalReadings);
    }

}
