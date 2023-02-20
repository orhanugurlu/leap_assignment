package energy.leap.meterhub.web;

import energy.leap.meterhub.service.MeterBatchReadingProcessorService;
import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingContentException;
import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingXmlException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeterReadingUploadController.class)
class MeterReadingUploadControllerTests {
    @MockBean
    MeterBatchReadingProcessorService meterBatchReadingProcessorService;

    @Autowired
    MockMvc mvc;

    private static final String METER_BATCH_READING_XML = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <ReadingType>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>0.07</kWhPrice>
                                <readingUnit>kWh</readingUnit>
                            </ReadingType>
                        </content>
                    </entry>
                    <entry>
                        <content>
                            <IntervalBlock>
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>3600</duration>
                                        <start>1555484400</start>
                                    </timePeriod>
                                    <value>200</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenConfigurationIsOk_WhenMeterBatchReadingReportPosted_ThenReadingsProcessed() throws Exception {
        // Arrange
        // None
        // Act
        MockMultipartFile file =
                new MockMultipartFile("file","meter.xml", MediaType.TEXT_PLAIN_VALUE, METER_BATCH_READING_XML.getBytes());
        mvc.perform(multipart("/upload").file(file))
        // Assert
        .andExpect(status().isOk());
        Mockito.verify(meterBatchReadingProcessorService, Mockito.times(1)).processBatchReading(METER_BATCH_READING_XML);
    }

    private static final String DUMMY_XML = "<dummy><dummy>";
    @Test
    void GivenServiceIsConfiguredToThrowIllegalXmlError_WhenXmlPosted_ThenErrorReturned() throws Exception {
        // Arrange
        Mockito.doThrow(new IllegalMeterBatchReadingXmlException("Invalid xml"))
                .when(meterBatchReadingProcessorService).processBatchReading(DUMMY_XML);
        // Act
        MockMultipartFile file =
                new MockMultipartFile("file","meter.xml", MediaType.TEXT_PLAIN_VALUE, DUMMY_XML.getBytes());
        mvc.perform(multipart("/upload").file(file))
        // Assert
        .andExpect(status().isInternalServerError());
        Mockito.verify(meterBatchReadingProcessorService, Mockito.times(1)).processBatchReading(DUMMY_XML);
    }

    @Test
    void GivenServiceIsConfiguredToThrowIllegalContentError_WhenXmlPosted_ThenErrorReturned() throws Exception {
        // Arrange
        Mockito.doThrow(new IllegalMeterBatchReadingContentException("Some message"))
                .when(meterBatchReadingProcessorService).processBatchReading(DUMMY_XML);
        // Act
        MockMultipartFile file =
                new MockMultipartFile("file","meter.xml", MediaType.TEXT_PLAIN_VALUE, DUMMY_XML.getBytes());
        mvc.perform(multipart("/upload").file(file))
        // Assert
        .andExpect(status().isInternalServerError());
        Mockito.verify(meterBatchReadingProcessorService, Mockito.times(1)).processBatchReading(DUMMY_XML);
    }

    @Test
    void GivenServiceIsConfiguredToThrowRuntimeError_WhenXmlPosted_ThenErrorReturned() throws Exception {
        // Arrange
        Mockito.doThrow(new RuntimeException("Some message"))
                .when(meterBatchReadingProcessorService).processBatchReading(DUMMY_XML);
        // Act
        MockMultipartFile file =
                new MockMultipartFile("file","meter.xml", MediaType.TEXT_PLAIN_VALUE, DUMMY_XML.getBytes());
        mvc.perform(multipart("/upload").file(file))
        // Assert
        .andExpect(status().isInternalServerError());
        Mockito.verify(meterBatchReadingProcessorService, Mockito.times(1)).processBatchReading(DUMMY_XML);
    }

}
