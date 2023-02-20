package energy.leap.meterhub.service;

import energy.leap.meterhub.data.entity.EnergyMeter;
import energy.leap.meterhub.data.entity.HourlyReading;
import energy.leap.meterhub.data.repository.EnergyMeterRepository;
import energy.leap.meterhub.data.repository.HourlyReadingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
class MeterBatchReadingProcessorServiceTests {

    @MockBean
    EnergyMeterRepository energyMeterRepository;

    @MockBean
    HourlyReadingRepository hourlyReadingRepository;

    @Autowired
    MeterBatchReadingProcessorService meterBatchReadingProcessorService;

    private static final String METER_BATCH_READING_XML_1 = """
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
    void GivenNoMeterReportExists_WhenNewMeterReportProcessed_ThenMeterIsSaved() {
        // Arrange
        Mockito.when(energyMeterRepository.findById("9346bfb3-20aa-3412-ffab-44f88b917999")).thenReturn(Optional.empty());
        // Act
        meterBatchReadingProcessorService.processBatchReading(METER_BATCH_READING_XML_1);
        // Assert
        EnergyMeter expectedMeter = new EnergyMeter("9346bfb3-20aa-3412-ffab-44f88b917999", "Green Button Usage Feed");
        Mockito.verify(energyMeterRepository, Mockito.times(1)).findById("9346bfb3-20aa-3412-ffab-44f88b917999");
        Mockito.verify(energyMeterRepository, Mockito.times(1)).save(expectedMeter);
    }

    @Test
    void GivenMeterReportExists_WhenNewMeterReportProcessed_ThenMeterIsUpdated() {
        // Arrange
        Mockito.when(energyMeterRepository.findById("9346bfb3-20aa-3412-ffab-44f88b917999")).thenReturn(Optional.of(new EnergyMeter("9346bfb3-20aa-3412-ffab-44f88b917999", "Green Button Usage Feed")));
        // Act
        meterBatchReadingProcessorService.processBatchReading(METER_BATCH_READING_XML_1);
        // Assert
        EnergyMeter expectedMeter = new EnergyMeter("9346bfb3-20aa-3412-ffab-44f88b917999", "Green Button Usage Feed");
        Mockito.verify(energyMeterRepository, Mockito.times(1)).findById("9346bfb3-20aa-3412-ffab-44f88b917999");
        Mockito.verify(energyMeterRepository, Mockito.times(1)).save(expectedMeter);
    }

    private static final String METER_BATCH_READING_XML_2 = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <ReadingType>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>0.07</kWhPrice>
                                <readingUnit>Wh</readingUnit>
                            </ReadingType>
                        </content>
                    </entry>
                    <entry>
                        <content>
                            <IntervalBlock>
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>3600</duration>
                                        <start>1555488000</start>
                                    </timePeriod>
                                    <value>260000</value>
                                </IntervalReading>
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>900</duration>
                                        <start>1555491600</start>
                                    </timePeriod>
                                    <value>65000</value>
                                </IntervalReading>
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>900</duration>
                                        <start>1555492500</start>
                                    </timePeriod>
                                    <value>35000</value>
                                </IntervalReading>
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>900</duration>
                                        <start>1555493400</start>
                                    </timePeriod>
                                    <value>51000</value>
                                </IntervalReading>
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>900</duration>
                                        <start>1555494300</start>
                                    </timePeriod>
                                    <value>49000</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenNoMeterReportExists_WhenReportWithReadingsProcessed_ThenReadingsSaved() {
        // Arrange
        // None
        // Act
        meterBatchReadingProcessorService.processBatchReading(METER_BATCH_READING_XML_2);
        // Assert
        List<HourlyReading> readingsExpected =
                Stream.of(new HourlyReading("9346bfb3-20aa-3412-ffab-44f88b917999", 1555488000L, new BigDecimal("0.07"), 260000L),
                                new HourlyReading("9346bfb3-20aa-3412-ffab-44f88b917999", 1555491600L, new BigDecimal("0.07"), 200000L))
                        .toList();
        Mockito.verify(hourlyReadingRepository, Mockito.times(1)).saveAll(readingsExpected);
    }
}
