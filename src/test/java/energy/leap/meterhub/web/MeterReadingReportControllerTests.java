package energy.leap.meterhub.web;

import energy.leap.meterhub.service.MeterReadingReportService;
import energy.leap.meterhub.service.dto.EnergyMeterDto;
import energy.leap.meterhub.service.dto.HourlyReportDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeterReadingReportController.class)
public class MeterReadingReportControllerTests {

    @MockBean
    MeterReadingReportService meterReadingReportService;

    @Autowired
    MockMvc mvc;

    @Test
    void GivenReportServiceTaughtToReturnCertainMeters_WhenMetersRequested_ThenCertainMetersReturned() throws Exception {
        // Arrange
        Mockito.when(meterReadingReportService.getEnergyMeters())
                .thenReturn(Stream.of(new EnergyMeterDto("dummy_id_1", "dummy title 1"),
                        new EnergyMeterDto("dummy_id_2", "dummy title 2"))
                .toList());
        // Act
        mvc.perform(get("/report/meters").contentType(MediaType.APPLICATION_JSON))
        // Assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is("dummy_id_1")))
        .andExpect(jsonPath("$[0].title", is("dummy title 1")))
        .andExpect(jsonPath("$[1].id", is("dummy_id_2")))
        .andExpect(jsonPath("$[1].title", is("dummy title 2")));
        Mockito.verify(meterReadingReportService, Mockito.times(1)).getEnergyMeters();
    }

    @Test
    void GivenReportServiceTaughtToReturnCertainTotalReading_WhenTotalReadingForMeterRequested_ThenTotalReadinForMeterReturned() throws Exception {
        // Arrange
        Mockito.when(meterReadingReportService.getTotalReadingAsWhForMeter("dummy_id_1"))
                .thenReturn(1000L);
        // Act
        mvc.perform(get("/report/total_reading/dummy_id_1").contentType(MediaType.APPLICATION_JSON))
        // Assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(1000)));
        Mockito.verify(meterReadingReportService, Mockito.times(1)).getTotalReadingAsWhForMeter("dummy_id_1");
    }

    @Test
    void GivenReportServiceTaughtToReturnCertainTotalCost_WhenTotalCostForMeterRequested_ThenTotalCostForMeterReturned() throws Exception {
        // Arrange
        Mockito.when(meterReadingReportService.getTotalCostForMeter("dummy_id_1"))
                .thenReturn(1000.0);
        // Act
        mvc.perform(get("/report/total_cost/dummy_id_1").contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1000.0)));
        Mockito.verify(meterReadingReportService, Mockito.times(1)).getTotalCostForMeter("dummy_id_1");
    }

    @Test
    void GivenReportServiceTaughtToReturnCertainReadings_WhenReadingsForMeterRequested_ThenReadingsForMeterReturned() throws Exception {
        // Arrange
        Mockito.when(meterReadingReportService.getHourlyReportsForMeter("dummy_id_1"))
                .thenReturn(Stream.of(new HourlyReportDto(1555484400L, 100L, new BigDecimal("0.007")),
                                new HourlyReportDto(1555488000L, 200L, new BigDecimal("0.016")))
                        .toList());
        // Act
        mvc.perform(get("/report/hourly_report/dummy_id_1").contentType(MediaType.APPLICATION_JSON))
        // Assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].hourStartEpochAsSec", is(1555484400)))
        .andExpect(jsonPath("$[0].readingAsWh", is(100)))
        .andExpect(jsonPath("$[0].cost", is(0.007)))
        .andExpect(jsonPath("$[1].hourStartEpochAsSec", is(1555488000)))
        .andExpect(jsonPath("$[1].readingAsWh", is(200)))
        .andExpect(jsonPath("$[1].cost", is(0.016)));
        Mockito.verify(meterReadingReportService, Mockito.times(1)).getHourlyReportsForMeter("dummy_id_1");
    }
}
