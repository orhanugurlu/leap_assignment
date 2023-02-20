package energy.leap.meterhub.data.repository;

import energy.leap.meterhub.data.entity.HourlyReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class HourlyReadingRepositoryTests {

    @Autowired
    HourlyReadingRepository hourlyReadingRepository;

    @Test
    void GivenReadingsSaved_WhenReadingsQueried_ThenSavedReadingsAreFound() {
        // Arrange
        List<HourlyReading> readings =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList();
        hourlyReadingRepository.saveAll(readings);
        // Act
        List<HourlyReading> foundReadings = hourlyReadingRepository.findAll();
        // Assert
        assertEquals(readings, foundReadings);
    }

    @Test
    void GivenTwoDifferentMeterReadingsSaved_WhenAllReadingsQueried_ThenSavedReadingsAreFound() {
        // Arrange
        List<HourlyReading> readings1 =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList();
        List<HourlyReading> readings2 =
                Stream.of(new HourlyReading("dummy_id_2", 1555484400L, new BigDecimal("0.01"), 500L),
                                new HourlyReading("dummy_id_2", 1555488000L, new BigDecimal("0.02"), 500L),
                                new HourlyReading("dummy_id_2", 1555477200L, new BigDecimal("0.03"), 600L))
                        .toList();
        hourlyReadingRepository.saveAll(readings1);
        hourlyReadingRepository.saveAll(readings2);
        // Act
        List<HourlyReading> foundReadings = hourlyReadingRepository.findAll();
        // Assert
        assertThat(foundReadings).containsAll(readings1).containsAll(readings2).hasSize(readings1.size() + readings2.size());
    }

    @Test
    void GivenReadingListsWithOverlappingReadingsSaved_WhenAllReadingsQueried_ThenUnionOfReadingsAreFound() {
        // Arrange
        List<HourlyReading> readings1 =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.05"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.06"), 200L))
                        .toList();
        List<HourlyReading> readings2 =
                Stream.of(new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.07"), 300L),
                                new HourlyReading("dummy_id_1", 1555495200L, new BigDecimal("0.08"), 400L))
                        .toList();
        hourlyReadingRepository.saveAll(readings1);
        hourlyReadingRepository.saveAll(readings2);
        // Act
        List<HourlyReading> foundReadings = hourlyReadingRepository.findAll();
        System.out.println(foundReadings);
        // Assert
        List<HourlyReading> expectedReadings =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.05"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.07"), 300L),
                                new HourlyReading("dummy_id_1", 1555495200L, new BigDecimal("0.08"), 400L))
                        .toList();
        assertThat(foundReadings).containsAll(expectedReadings).hasSize(expectedReadings.size());
    }

    @Test
    void GivenTwoDifferentMeterReadingsSaved_WhenFirstMeterReadingsQueried_ThenOnlyFirstMeterReadingsAreFound() {
        // Arrange
        List<HourlyReading> readings1 =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList();
        List<HourlyReading> readings2 =
                Stream.of(new HourlyReading("dummy_id_2", 1555484400L, new BigDecimal("0.01"), 500L),
                                new HourlyReading("dummy_id_2", 1555488000L, new BigDecimal("0.02"), 500L),
                                new HourlyReading("dummy_id_2", 1555477200L, new BigDecimal("0.03"), 600L))
                        .toList();
        hourlyReadingRepository.saveAll(readings1);
        hourlyReadingRepository.saveAll(readings2);
        // Act
        List<HourlyReading> foundReadings1 = hourlyReadingRepository.findByIdMeterId("dummy_id_1");
        // Assert
        assertThat(foundReadings1).containsAll(readings1).hasSize(readings1.size());
    }

    @Test
    void GivenTwoDifferentMeterReadingsSaved_WhenSecondMeterReadingsQueried_ThenOnlySecondMeterReadingsAreFound() {
        // Arrange
        List<HourlyReading> readings1 =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList();
        List<HourlyReading> readings2 =
                Stream.of(new HourlyReading("dummy_id_2", 1555484400L, new BigDecimal("0.01"), 500L),
                                new HourlyReading("dummy_id_2", 1555488000L, new BigDecimal("0.02"), 500L),
                                new HourlyReading("dummy_id_2", 1555477200L, new BigDecimal("0.03"), 600L))
                        .toList();
        hourlyReadingRepository.saveAll(readings1);
        hourlyReadingRepository.saveAll(readings2);
        // Act
        List<HourlyReading> foundReadings2 = hourlyReadingRepository.findByIdMeterId("dummy_id_2");
        // Assert
        assertThat(foundReadings2).containsAll(readings2).hasSize(readings2.size());
    }

    @Test
    void GivenReadingsForMeterSaved_WhenTotalReadingForMeterQueried_ThenTotalIsEqualToSumOfMeterReadings() {
        // Arrange
        List<HourlyReading> readings =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList();
        hourlyReadingRepository.saveAll(readings);
        // Act
        Long totalReading = hourlyReadingRepository.getTotalReadingAsWhOfMeter("dummy_id_1");
        // Assert
        assertEquals(600L, totalReading);
    }

    @Test
    void GivenReadingsForMeterSaved_WhenTotalCostForMeterQueried_ThenTotalIsEqualToSumOfReadingsMultipliedByPrices() {
        // Arrange
        List<HourlyReading> readings =
                Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList();
        hourlyReadingRepository.saveAll(readings);
        // Act
        Double totalReading = hourlyReadingRepository.getTotalCostOfMeter("dummy_id_1");
        // Assert
        assertEquals(0.05, totalReading, 1E-6);
    }
}
