package energy.leap.meterhub;

import energy.leap.meterhub.service.dto.EnergyMeterDto;
import energy.leap.meterhub.service.dto.HourlyReportDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MeterhubApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    void GivenConfigurationOk_WhenContextLoaded_NoErrorsOccur() {
        // Arrange
        // Configuration is already the code itself
        // Act
        // SpringBootTest loads the context
        // Assert
        // If no errors, we reach here
        assertTrue(true);
    }

    @Test
    void GivenNoReportUploaded_WhenReportRequested_ThenEmptyResponseReceived() {
        // Arrange
        // None
        // Act
        final ResponseEntity<EnergyMeterDto[]> response =
                restTemplate.getForEntity(String.format("http://localhost:%d/report/meters", randomServerPort), EnergyMeterDto[].class);
        // Assert
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void GivenNoReportUploaded_WhenTotalReadingForAMeterRequested_ThenException() {
        // Arrange
        // None
        // Act
        String baseUrl = String.format("http://localhost:%d", randomServerPort);
        URI uriTotalReadingReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_reading/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Void> totalCostResponse = restTemplate.getForEntity(uriTotalReadingReport, Void.class);
        // Assert
        Assertions.assertThat(totalCostResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Void> uploadFile(String baseUrl, String filePath) {
        URI uriUpload = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/upload").build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(uriUpload, requestEntity, Void.class);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void GivenOneReportUploaded_WhenReportRequested_ThenCorrectReportReceived() {
        // Arrange
        String baseUrl = String.format("http://localhost:%d", randomServerPort);
        ResponseEntity<Void> uploadResponse = uploadFile(baseUrl, "src/test/resources/meter1.xml");
        Assertions.assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Act
        URI uriMetersReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/meters").build().toUri();
        final ResponseEntity<EnergyMeterDto[]> reportResponse = restTemplate.getForEntity(uriMetersReport, EnergyMeterDto[].class);
        // Assert
        Assertions.assertThat(reportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(reportResponse.getBody());
        assertEquals(1, reportResponse.getBody().length);
        assertEquals("1a46b097-b80a-4e25-8852-44f88b9179ae", reportResponse.getBody()[0].getId());
        assertEquals("Green Button Usage Feed", reportResponse.getBody()[0].getTitle());

        // Act
        URI uriTotalReadingReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_reading/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Long> totalCostResponse = restTemplate.getForEntity(uriTotalReadingReport, Long.class);
        // Assert
        Assertions.assertThat(totalCostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(800000L, totalCostResponse.getBody());

        // Act
        URI uriTotalCostReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_cost/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Double> totalCastResponse = restTemplate.getForEntity(uriTotalCostReport, Double.class);
        // Assert
        Assertions.assertThat(totalCastResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(56.0, totalCastResponse.getBody());

        // Act
        URI uriHourlyReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/hourly_report/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<HourlyReportDto[]> hourlyReportResponse = restTemplate.getForEntity(uriHourlyReport, HourlyReportDto[].class);
        // Assert
        Assertions.assertThat(hourlyReportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(hourlyReportResponse.getBody());
        assertEquals(3, hourlyReportResponse.getBody().length);
        List<HourlyReportDto> actualReports = Arrays.stream(hourlyReportResponse.getBody()).toList();
        List<HourlyReportDto> expectedReports =
                Stream.of(new HourlyReportDto(1555484400L, 340000L, new BigDecimal("23.80")),
                                new HourlyReportDto(1555488000L, 260000L, new BigDecimal("18.20")),
                                new HourlyReportDto(1555491600L, 200000L, new BigDecimal("14.00")))
                        .toList();
        Assertions.assertThat(actualReports).containsAll(expectedReports);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void GivenReportsForMultipleMetersUploaded_WhenReportRequestedForEachMeter_ThenCorrectReportsReceived() {
        // Arrange
        String baseUrl = String.format("http://localhost:%d", randomServerPort);
        ResponseEntity<Void> uploadResponse = uploadFile(baseUrl, "src/test/resources/meter1.xml");
        Assertions.assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> uploadResponse2 = uploadFile(baseUrl, "src/test/resources/meter2.xml");
        Assertions.assertThat(uploadResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Act
        URI uriMetersReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/meters").build().toUri();
        final ResponseEntity<EnergyMeterDto[]> reportResponse = restTemplate.getForEntity(uriMetersReport, EnergyMeterDto[].class);
        // Assert
        Assertions.assertThat(reportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(reportResponse.getBody());
        assertEquals(2, reportResponse.getBody().length);
        List<EnergyMeterDto> actualMeters = Arrays.stream(reportResponse.getBody()).toList();
        List<EnergyMeterDto> expectedMeters =
                Stream.of(new EnergyMeterDto("1a46b097-b80a-4e25-8852-44f88b9179ae", "Green Button Usage Feed"),
                                new EnergyMeterDto("9346bfb3-20aa-3412-ffab-44f88b917999", "Green Button Usage Feed"))
                        .toList();
        Assertions.assertThat(actualMeters).containsAll(expectedMeters);

        // Act
        URI uriTotalReadingReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_reading/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Long> totalCostResponse = restTemplate.getForEntity(uriTotalReadingReport, Long.class);
        URI uriTotalReadingReport2 = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_reading/9346bfb3-20aa-3412-ffab-44f88b917999").build().toUri();
        final ResponseEntity<Long> totalCostResponse2 = restTemplate.getForEntity(uriTotalReadingReport2, Long.class);
        // Assert
        Assertions.assertThat(totalCostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(800000L, totalCostResponse.getBody());
        assertEquals(660000L, totalCostResponse2.getBody());

        // Act
        URI uriTotalCostReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_cost/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Double> totalCastResponse = restTemplate.getForEntity(uriTotalCostReport, Double.class);
        URI uriTotalCostReport2 = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_cost/9346bfb3-20aa-3412-ffab-44f88b917999").build().toUri();
        final ResponseEntity<Double> totalCastResponse2 = restTemplate.getForEntity(uriTotalCostReport2, Double.class);
        // Assert
        Assertions.assertThat(totalCastResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(56.0, totalCastResponse.getBody());
        assertEquals(46.2, totalCastResponse2.getBody());

        // Act
        URI uriHourlyReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/hourly_report/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<HourlyReportDto[]> hourlyReportResponse = restTemplate.getForEntity(uriHourlyReport, HourlyReportDto[].class);
        URI uriHourlyReport2 = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/hourly_report/9346bfb3-20aa-3412-ffab-44f88b917999").build().toUri();
        final ResponseEntity<HourlyReportDto[]> hourlyReportResponse2 = restTemplate.getForEntity(uriHourlyReport2, HourlyReportDto[].class);
        // Assert
        Assertions.assertThat(hourlyReportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(hourlyReportResponse.getBody());
        assertEquals(3, hourlyReportResponse.getBody().length);
        List<HourlyReportDto> actualReports = Arrays.stream(hourlyReportResponse.getBody()).toList();
        List<HourlyReportDto> expectedReports =
                Stream.of(new HourlyReportDto(1555484400L, 340000L, new BigDecimal("23.80")),
                                new HourlyReportDto(1555488000L, 260000L, new BigDecimal("18.20")),
                                new HourlyReportDto(1555491600L, 200000L, new BigDecimal("14.00")))
                        .toList();
        Assertions.assertThat(actualReports).containsAll(expectedReports);
        Assertions.assertThat(hourlyReportResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(hourlyReportResponse2.getBody());
        assertEquals(3, hourlyReportResponse2.getBody().length);
        List<HourlyReportDto> actualReports2 = Arrays.stream(hourlyReportResponse2.getBody()).toList();
        List<HourlyReportDto> expectedReports2 =
                Stream.of(new HourlyReportDto(1555484400L, 200000L, new BigDecimal("14.00")),
                                new HourlyReportDto(1555488000L, 260000L, new BigDecimal("18.20")),
                                new HourlyReportDto(1555491600L, 200000L, new BigDecimal("14.00")))
                        .toList();
        Assertions.assertThat(actualReports2).containsAll(expectedReports2);
    }

	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
	void GivenOverlappingReportsForAMeterUploaded_WhenReportRequested_ThenCorrectReportReceived() {
        // Arrange
        String baseUrl = String.format("http://localhost:%d", randomServerPort);
        ResponseEntity<Void> uploadResponse = uploadFile(baseUrl, "src/test/resources/meter1.xml");
        Assertions.assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> uploadResponse2 = uploadFile(baseUrl, "src/test/resources/meter1_2.xml");
        Assertions.assertThat(uploadResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Act
        URI uriMetersReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/meters").build().toUri();
        final ResponseEntity<EnergyMeterDto[]> reportResponse = restTemplate.getForEntity(uriMetersReport, EnergyMeterDto[].class);
        // Assert
        Assertions.assertThat(reportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(reportResponse.getBody());
        assertEquals(1, reportResponse.getBody().length);
        List<EnergyMeterDto> actualMeters = Arrays.stream(reportResponse.getBody()).toList();
        List<EnergyMeterDto> expectedMeters =
                Stream.of(new EnergyMeterDto("1a46b097-b80a-4e25-8852-44f88b9179ae", "Green Button Usage Feed"))
                        .toList();
        Assertions.assertThat(actualMeters).containsAll(expectedMeters);

        // Act
        URI uriTotalReadingReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_reading/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Long> totalCostResponse = restTemplate.getForEntity(uriTotalReadingReport, Long.class);
        // Assert
        Assertions.assertThat(totalCostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(960000L, totalCostResponse.getBody());

        // Act
        URI uriTotalCostReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/total_cost/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<Double> totalCastResponse = restTemplate.getForEntity(uriTotalCostReport, Double.class);
        // Assert
        Assertions.assertThat(totalCastResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(57.2, totalCastResponse.getBody());

        // Act
        URI uriHourlyReport = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/report/hourly_report/1a46b097-b80a-4e25-8852-44f88b9179ae").build().toUri();
        final ResponseEntity<HourlyReportDto[]> hourlyReportResponse = restTemplate.getForEntity(uriHourlyReport, HourlyReportDto[].class);
        // Assert
        Assertions.assertThat(hourlyReportResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(hourlyReportResponse.getBody());
        assertEquals(4, hourlyReportResponse.getBody().length);
        List<HourlyReportDto> actualReports = Arrays.stream(hourlyReportResponse.getBody()).toList();
        List<HourlyReportDto> expectedReports =
                Stream.of(new HourlyReportDto(1555480800L, 100000L, new BigDecimal("5.00")),
                                new HourlyReportDto(1555484400L, 400000L, new BigDecimal("20.00")),
                                new HourlyReportDto(1555488000L, 260000L, new BigDecimal("18.20")),
                                new HourlyReportDto(1555491600L, 200000L, new BigDecimal("14.00")))
                        .toList();
        Assertions.assertThat(actualReports).containsAll(expectedReports);
    }

    @Test
    void GivenConfigurationOk__WhenInvalidReportUploaded_ThenErrorReceived() {
        // Arrange
        // None
        // Act
        String baseUrl = String.format("http://localhost:%d", randomServerPort);
        ResponseEntity<Void> uploadResponse = uploadFile(baseUrl, "src/test/resources/invalid_meter.xml");
        // Assert
        Assertions.assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
