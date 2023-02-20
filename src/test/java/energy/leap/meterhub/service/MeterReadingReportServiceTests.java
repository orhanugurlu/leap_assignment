package energy.leap.meterhub.service;

import energy.leap.meterhub.data.entity.EnergyMeter;
import energy.leap.meterhub.data.entity.HourlyReading;
import energy.leap.meterhub.data.repository.EnergyMeterRepository;
import energy.leap.meterhub.data.repository.HourlyReadingRepository;
import energy.leap.meterhub.service.dto.EnergyMeterDto;
import energy.leap.meterhub.service.dto.HourlyReportDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MeterReadingReportServiceTests {

    @MockBean
    EnergyMeterRepository energyMeterRepository;

    @MockBean
    HourlyReadingRepository hourlyReadingRepository;

    @Autowired
    MeterReadingReportService meterReadingReportService;

    @Test
    void GivenMockRepoTaughtToReturnCertainCost_WhenTotalCostForMeterRequested_ThenCertainCostReturned() {
        // Arrange
        Mockito.when(hourlyReadingRepository.getTotalCostOfMeter("dummy_id")).thenReturn(100.0);
        // Act
        Double totalCost = meterReadingReportService.getTotalCostForMeter("dummy_id");
        // Assert
        Mockito.verify(hourlyReadingRepository, Mockito.times(1)).getTotalCostOfMeter("dummy_id");
        assertEquals(100.0, totalCost);
    }

    @Test
    void GivenMockRepoTaughtToReturnCertainTotal_WhenTotalReadingForMeterRequested_ThenCertainTotalReturned() {
        // Arrange
        Mockito.when(hourlyReadingRepository.getTotalReadingAsWhOfMeter("dummy_id")).thenReturn(1000L);
        // Act
        Long totalReading = meterReadingReportService.getTotalReadingAsWhForMeter("dummy_id");
        // Assert
        Mockito.verify(hourlyReadingRepository, Mockito.times(1)).getTotalReadingAsWhOfMeter("dummy_id");
        assertEquals(1000, totalReading);
    }

    @Test
    void GivenMockRepoTaughtToReturnCertainMeters_WhenMetersRequested_ThenCertainMetersReturned() {
        // Arrange
        Mockito.when(energyMeterRepository.findAll())
                .thenReturn(Stream.of(new EnergyMeter("dummy_id_1", "dummy title 1"),
                                new EnergyMeter("dummy_id_2", "dummy title 2"))
                        .toList());
        // Act
        List<EnergyMeterDto> meters = meterReadingReportService.getEnergyMeters();
        // Assert
        Mockito.verify(energyMeterRepository, Mockito.times(1)).findAll();
        List<EnergyMeterDto> expectedMeters = Stream.of(new EnergyMeterDto("dummy_id_1", "dummy title 1"),
                        new EnergyMeterDto("dummy_id_2", "dummy title 2"))
                .toList();
        Assertions.assertThat(meters).containsAll(expectedMeters).hasSize(expectedMeters.size());
    }

    @Test
    void GivenMockRepoTaughtToReturnCertainHourlyReports_WhenHourlyReportsRequested_ThenCertainHourlyReportsReturned() {
        // Arrange
        Mockito.when(hourlyReadingRepository.findByIdMeterId("dummy_id_1"))
                .thenReturn(Stream.of(new HourlyReading("dummy_id_1", 1555484400L, new BigDecimal("0.07"), 100L),
                                new HourlyReading("dummy_id_1", 1555488000L, new BigDecimal("0.08"), 200L),
                                new HourlyReading("dummy_id_1", 1555477200L, new BigDecimal("0.09"), 300L))
                        .toList());
        // Act
        List<HourlyReportDto> hourlyReports = meterReadingReportService.getHourlyReportsForMeter("dummy_id_1");
        // Assert
        Mockito.verify(hourlyReadingRepository, Mockito.times(1)).findByIdMeterId("dummy_id_1");
        List<HourlyReportDto> expectedHourlyReports =
                Stream.of(new HourlyReportDto(1555484400L, 100L, new BigDecimal("0.007")),
                                new HourlyReportDto(1555488000L, 200L, new BigDecimal("0.016")),
                                new HourlyReportDto(1555477200L, 300L, new BigDecimal("0.027")))
                .toList();
        Assertions.assertThat(hourlyReports).containsAll(expectedHourlyReports).hasSize(expectedHourlyReports.size());
    }
}
