package energy.leap.meterhub.service.impl.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlMeterBatchReadingTests {

    @Test
    void GivenNonOverlappingIntervals_WhenOverlapChecked_ThenNoOverlapDetected() {
        // Arrange
        XmlIntervalReading intervalReading1 = new XmlIntervalReading(1555484400L, 3600L, 0L);
        XmlIntervalReading intervalReading2 = new XmlIntervalReading(1555491600L, 3600L, 0L);
        // Act
        boolean result = intervalReading1.overlaps(intervalReading2);
        // Assert
        assertFalse(result);
    }

    @Test
    void GivenOverlappingIntervals_WhenOverlapChecked_ThenOverlapDetected() {
        // Arrange
        XmlIntervalReading intervalReading1 = new XmlIntervalReading(1555484400L, 3600L, 0L);
        XmlIntervalReading intervalReading2 = new XmlIntervalReading(1555484800L, 3600L, 0L);
        // Act
        boolean result = intervalReading1.overlaps(intervalReading2);
        // Assert
        assertTrue(result);
    }

    @Test
    void GivenConsecutiveHourIntervals_WhenOverlapChecked_ThenNoOverlapDetected() {
        // Arrange
        XmlIntervalReading intervalReading1 = new XmlIntervalReading(1555484400L, 3600L, 0L);
        XmlIntervalReading intervalReading2 = new XmlIntervalReading(1555488000L, 3600L, 0L);
        // Act
        boolean result = intervalReading1.overlaps(intervalReading2);
        // Assert
        assertFalse(result);
    }
}
