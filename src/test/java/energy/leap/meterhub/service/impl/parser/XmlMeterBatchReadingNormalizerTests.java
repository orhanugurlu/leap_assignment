package energy.leap.meterhub.service.impl.parser;

import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingContentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlMeterBatchReadingNormalizerTests {

    private XmlMeterBatchReading createTestReadingWithIntervals(List<XmlIntervalReading> readings) {
        return new XmlMeterBatchReading("dummy_id", "dummy title", new BigDecimal("0.07"), XmlReadingUnit.KWH, readings);
    }

    @Test
    void GivenReadingWithHourlyIntervalsOnly_WhenNormalized_ThenNormalizedIsSame() {
        // Arrange
        XmlMeterBatchReading reading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 100L),
                        new XmlIntervalReading(1555488000L, 3600L, 100L),
                        new XmlIntervalReading(1555477200L, 3600L, 100L)));
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        // Act
        XmlMeterBatchReading normalizedReading = normalizer.normalize(reading);
        // Assert
        assertEquals(reading, normalizedReading);
    }

    @Test
    void GivenReadingWithPartedButCompleteIntervals_WhenNormalized_ThenPartsAreCombined() {
        // Arrange
        XmlMeterBatchReading reading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 1000L),
                        new XmlIntervalReading(1555488000L, 3600L, 2000L),
                        new XmlIntervalReading(1555494300L, 900L, 100L),
                        new XmlIntervalReading(1555491600L, 900L, 150L),
                        new XmlIntervalReading(1555492500L, 900L, 200L),
                        new XmlIntervalReading(1555493400L, 900L, 250L)));
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        // Act
        XmlMeterBatchReading normalizedReading = normalizer.normalize(reading);
        // Assert
        XmlMeterBatchReading expectedReading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 1000L),
                        new XmlIntervalReading(1555488000L, 3600L, 2000L),
                        new XmlIntervalReading(1555491600L, 3600L, 700L)));
        assertEquals(expectedReading, normalizedReading);
    }

    @Test
    void GivenReadingWithIncompleteIntervals_WhenNormalized_ThenExceptionThrown() {
        // Arrange
        XmlMeterBatchReading reading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 1000L),
                        new XmlIntervalReading(1555488000L, 3600L, 2000L),
                        new XmlIntervalReading(1555491600L, 900L, 150L),
                        new XmlIntervalReading(1555492500L, 900L, 200L),
                        new XmlIntervalReading(1555493400L, 900L, 250L)));
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        // Act
        assertThatCode(() -> normalizer.normalize(reading))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingContentException.class)
        .hasMessageContaining("Partial hour");
    }

    @Test
    void GivenReadingWithOverlappingIntervals_WhenNormalized_ThenExceptionThrown() {
        // Arrange
        XmlMeterBatchReading reading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 1000L),
                        new XmlIntervalReading(1555488000L, 3600L, 2000L),
                        new XmlIntervalReading(1555494300L, 900L, 100L),
                        new XmlIntervalReading(1555491600L, 1800L, 150L),
                        new XmlIntervalReading(1555492500L, 900L, 200L),
                        new XmlIntervalReading(1555493400L, 900L, 250L)));
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        // Act
        assertThatCode(() -> normalizer.normalize(reading))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingContentException.class)
        .hasMessageContaining("overlaps with");
    }

    @Test
    void GivenReadingWithOverflowsToNextHour_WhenNormalized_ThenExceptionThrown() {
        // Arrange
        XmlMeterBatchReading reading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 1000L),
                        new XmlIntervalReading(1555488000L, 3600L, 2000L),
                        new XmlIntervalReading(1555494300L, 1800L, 100L),
                        new XmlIntervalReading(1555491600L, 900L, 150L),
                        new XmlIntervalReading(1555492500L, 900L, 200L),
                        new XmlIntervalReading(1555493400L, 900L, 250L)));
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        // Act
        assertThatCode(() -> normalizer.normalize(reading))
                // Assert
                .isInstanceOf(IllegalMeterBatchReadingContentException.class)
                .hasMessageContaining("overflows hour");
    }

    @Test
    void GivenReadingWithDisconnectedParts_WhenNormalized_ThenExceptionThrown() {
        // Arrange
        XmlMeterBatchReading reading = createTestReadingWithIntervals(
                List.of(new XmlIntervalReading(1555484400L, 3600L, 1000L),
                        new XmlIntervalReading(1555488000L, 3600L, 2000L),
                        new XmlIntervalReading(1555494300L, 900L, 100L),
                        new XmlIntervalReading(1555491600L, 900L, 150L),
                        new XmlIntervalReading(1555492500L, 450L, 200L),
                        new XmlIntervalReading(1555493400L, 900L, 250L)));
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        // Act
        assertThatCode(() -> normalizer.normalize(reading))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingContentException.class)
        .hasMessageContaining("Disconnected interval exists");
    }

}
