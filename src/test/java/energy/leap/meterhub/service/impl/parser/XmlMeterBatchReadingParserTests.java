package energy.leap.meterhub.service.impl.parser;

import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingXmlException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlMeterBatchReadingParserTests {

    private static final String VALID_XML = """
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
                                <IntervalReading>
                                    <timePeriod>
                                        <duration>900</duration>
                                        <start>1555488000</start>
                                    </timePeriod>
                                    <value>260</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenValidXml_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(VALID_XML))
        // Assert
        .doesNotThrowAnyException();
    }

    @Test
    void GivenValidXml_WhenParsed_ThenParseResultSameAsFile() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        XmlMeterBatchReading reading = parser.parseReadingFile(VALID_XML);
        // Assert
        assertEquals(new XmlMeterBatchReading("9346bfb3-20aa-3412-ffab-44f88b917999",
                "Green Button Usage Feed",
                new BigDecimal("0.07"),
                XmlReadingUnit.KWH,
                List.of(new XmlIntervalReading(1555484400L, 3600L, 200L),
                        new XmlIntervalReading(1555488000L, 900L, 260L)
                )), reading);
    }

    private static final String ILLEGAL_XML = "<dummy><dummy>";

    @Test
    void GivenIllegalXml_WhenParsed_ThenIllegalFormattedReadingExceptionThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(ILLEGAL_XML))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Parsing error");
    }
    private static final String XML_WITH_WRONG_ROOT = "<dummy></dummy>";

    @Test
    void GivenXmlWithWrongRoot_WhenParsed_ThenIllegalFormattedReadingExceptionThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_WRONG_ROOT))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid root node");
    }

    private static final String XML_MISSING_ENTRY = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
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
    void GivenXmlWithMissingEntry_WhenParsed_ThenIllegalFormattedReadingExceptionThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_MISSING_ENTRY))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of entry nodes");
    }

    private static final String XML_WITH_MULTIPLE_CONTENTS = """
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
    void GivenXmlWithMultipleContents_WhenParsed_ThenIllegalFormattedReadingExceptionThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_CONTENTS))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of content");
    }


    private static final String XML_MISSING_READING_TYPE = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <Dummy>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>0.07</kWhPrice>
                                <readingUnit>kWh</readingUnit>
                            </Dummy>
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
    void GivenXmlWithMissingReadingType_WhenParsed_ThenIllegalFormattedReadingExceptionThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_MISSING_READING_TYPE))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("ReadingType node is missing");
    }

    private static final String XML_WITH_READING_TYPE_INTERVAL_BLOCK_UNDER_SAME_ENTRY = """
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
    void GivenXmlWithReadingTypeIntervalBlockUnderSameEntry_WhenParsed_ThenIllegalFormattedReadingExceptionThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_READING_TYPE_INTERVAL_BLOCK_UNDER_SAME_ENTRY))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Both ReadingType and IntervalBlock exists under content node");
    }

    private static final String XML_WITH_NO_INTERVAL_READINGS = """
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
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithNoIntervalReadings_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_NO_INTERVAL_READINGS))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("No IntervalReadings exist");
    }

    private static final String XML_WITH_MULTIPLE_ID_NODES = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
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
    void GivenXmlMultipleIdNodes_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_ID_NODES))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of id nodes");
    }

    private static final String XML_WITH_MULTIPLE_TITLE_NODES = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
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
    void GivenXmlMultipleTitleNodes_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_TITLE_NODES))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of title nodes");
    }

    private static final String XML_WITH_MULTIPLE_READING_TYPES = """
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
    void GivenXmlMultipleReadingTypes_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_READING_TYPES))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Multiple ReadingType nodes");
    }

    private static final String XML_WITH_MULTIPLE_INTERVAL_BLOCKS = """
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
    void GivenXmlMultipleIntervalBlocks_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_INTERVAL_BLOCKS))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Multiple IntervalBlock nodes");
    }

    private static final String XML_WITH_MULTIPLE_PRICE = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <ReadingType>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>0.07</kWhPrice>
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
    void GivenXmlWithMultiplePrice_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_PRICE))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of kWhPrice");
    }

    private static final String XML_WITH_ILLEGAL_PRICE = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <ReadingType>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>dummy</kWhPrice>
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
    void GivenXmlWithIllegalPrice_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_ILLEGAL_PRICE))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid kWhPrice");
    }

    private static final String XML_WITH_MULTIPLE_UNIT = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <ReadingType>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>0.07</kWhPrice>
                                <readingUnit>kWh</readingUnit>
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
    void GivenXmlWithMultipleUnit_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_UNIT))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of readingUnit");
    }

    private static final String XML_WITH_ILLEGAL_UNIT = """
                <feed>
                    <id>9346bfb3-20aa-3412-ffab-44f88b917999</id>
                    <title type="text">Green Button Usage Feed</title>
                    <entry>
                        <content>
                            <ReadingType>
                                <flowDirection>1</flowDirection>
                                <kWhPrice>0.07</kWhPrice>
                                <readingUnit>dummy</readingUnit>
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
    void GivenXmlWithIllegalUnit_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_ILLEGAL_UNIT))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid readingUnit ");
    }

    private static final String XML_WITH_MULTIPLE_VALUES = """
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
                                    <value>200</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithMultipleValues_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_VALUES))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of value nodes");
    }

    private static final String XML_WITH_ILLEGAL_VALUE = """
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
                                    <value>dummy</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithIllegalValue_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_ILLEGAL_VALUE))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid value node value");
    }

    private static final String XML_WITH_MULTIPLE_PERIODS = """
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
                                    <timePeriod>
                                        <duration>3600</duration>
                                        <start>1555484400</start>
                                    </timePeriod>
                                    <value>3600</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithMultiplePeriods_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_PERIODS))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of timePeriod nodes");
    }

    private static final String XML_WITH_MULTIPLE_DURATIONS = """
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
                                        <duration>3600</duration>
                                        <start>1555484400</start>
                                    </timePeriod>
                                    <value>3600</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithMultipleDurations_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_DURATIONS))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of duration nodes");
    }

    private static final String XML_WITH_ILLEGAL_DURATION = """
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
                                        <duration>dummy</duration>
                                        <start>1555484400</start>
                                    </timePeriod>
                                    <value>3600</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithIllegalDuration_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_ILLEGAL_DURATION))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid duration node value");
    }

    private static final String XML_WITH_MULTIPLE_STARTS = """
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
                                        <start>1555484400</start>
                                    </timePeriod>
                                    <value>3600</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithMultipleStarts_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_MULTIPLE_STARTS))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid number of start nodes");
    }

    private static final String XML_WITH_ILLEGAL_START = """
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
                                        <start>dummy</start>
                                    </timePeriod>
                                    <value>3600</value>
                                </IntervalReading>
                            </IntervalBlock>
                        </content>
                    </entry>
                </feed>""";

    @Test
    void GivenXmlWithIllegalStart_WhenParsed_ThenNoExceptionsThrown() {
        // Arrange
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        // Act
        assertThatCode(() -> parser.parseReadingFile(XML_WITH_ILLEGAL_START))
        // Assert
        .isInstanceOf(IllegalMeterBatchReadingXmlException.class)
        .hasMessageContaining("Invalid start node value");
    }

}
