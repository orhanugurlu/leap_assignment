package energy.leap.meterhub.service.impl.parser;

import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingXmlException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

public class XmlMeterBatchReadingParser {

    public XmlMeterBatchReading parseReadingFile (String xmlContent) {
        XmlMeterBatchReading result = new XmlMeterBatchReading();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // to be compliant, completely disable DOCTYPE declaration:
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // or completely disable external entities declarations:
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // or prohibit the use of all protocols by external entities:
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            // or disable entity expansion but keep in mind that this doesn't prevent fetching external entities
            // and this solution is not correct for OpenJDK < 13 due to a bug: https://bugs.openjdk.java.net/browse/JDK-8206132
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
            Element root = doc.getDocumentElement();
            root.normalize();

            parseRootNode(result, root);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new IllegalMeterBatchReadingXmlException("Parsing error", e);
        }

        if (result.getPricePerKwh() == null) {
            throw new IllegalMeterBatchReadingXmlException("ReadingType node is missing");
        }

        if (result.getIntervalReadings().isEmpty()) {
            throw new IllegalMeterBatchReadingXmlException("No IntervalReadings exist");
        }
        return result;
    }

    private void parseRootNode(XmlMeterBatchReading result, Element root) {
        if (!root.getTagName().equals("feed")) {
            throw new IllegalMeterBatchReadingXmlException("Invalid root node name : '" + root.getTagName() + "'");
        }

        NodeList idNodes = root.getElementsByTagName("id");
        if (idNodes.getLength() == 1) {
            result.setId(idNodes.item(0).getTextContent());
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of id nodes : " + idNodes.getLength());
        }

        NodeList titleNodes = root.getElementsByTagName("title");
        if (titleNodes.getLength() == 1) {
            result.setTitle(titleNodes.item(0).getTextContent());
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of title nodes : " + titleNodes.getLength());
        }

        NodeList entryNodes = root.getElementsByTagName("entry");
        if (entryNodes.getLength() == 2) {
            for (int i = 0; i < entryNodes.getLength(); i++) {
                Element entryNode = (Element)entryNodes.item(i);
                parseEntryNode(result, entryNode);
            }
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of entry nodes : " + entryNodes.getLength());
        }
    }

    private void parseEntryNode(XmlMeterBatchReading result, Element entryNode) {
        NodeList contentNodes = entryNode.getElementsByTagName("content");
        if (contentNodes.getLength() == 1) {
            NodeList readingTypeNodes = entryNode.getElementsByTagName("ReadingType");
            NodeList intervalBlockNodes = entryNode.getElementsByTagName("IntervalBlock");
            if (readingTypeNodes.getLength() != 0 && intervalBlockNodes.getLength() != 0) {
                throw new IllegalMeterBatchReadingXmlException("Both ReadingType and IntervalBlock exists under content node");
            } else if (readingTypeNodes.getLength() > 1) {
                throw new IllegalMeterBatchReadingXmlException("Multiple ReadingType nodes under content node");
            } else if (intervalBlockNodes.getLength() > 1) {
                throw new IllegalMeterBatchReadingXmlException("Multiple IntervalBlock nodes under content node");
            }
            if (readingTypeNodes.getLength() == 1) {
                parseReadingTypeNode(result, (Element)readingTypeNodes.item(0));
            }
            if (intervalBlockNodes.getLength() == 1) {
                parseIntervalBlockNode(result, (Element)intervalBlockNodes.item(0));
            }
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of content under entry node : " + contentNodes.getLength());
        }
    }

    private void parseReadingTypeNode(XmlMeterBatchReading result, Element readingTypeNode) {
        NodeList kWhPriceNodes = readingTypeNode.getElementsByTagName("kWhPrice");
        if (kWhPriceNodes.getLength() == 1) {
            try {
                result.setPricePerKwh(new BigDecimal(kWhPriceNodes.item(0).getTextContent()));
            } catch (NumberFormatException nfe) {
                throw new IllegalMeterBatchReadingXmlException("Invalid kWhPrice value : " + kWhPriceNodes.item(0).getTextContent(), nfe);
            }
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of kWhPrice nodes under ReadingType node : " + kWhPriceNodes.getLength());
        }
        NodeList readingUnitNodes = readingTypeNode.getElementsByTagName("readingUnit");
        if (readingUnitNodes.getLength() == 1) {
            String readingUnitNodeValue = readingUnitNodes.item(0).getTextContent();
            try {
                result.setReadingUnit(XmlReadingUnit.valueOf((readingUnitNodeValue.toUpperCase())));
            } catch (IllegalArgumentException iae) {
                throw new IllegalMeterBatchReadingXmlException("Invalid readingUnit value : " + readingUnitNodeValue, iae);
            }
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of readingUnit nodes under ReadingType node : " + readingUnitNodes.getLength());
        }
    }

    private void parseIntervalBlockNode(XmlMeterBatchReading result, Element intervalBlockNode) {
        NodeList intervalReadingNodes = intervalBlockNode.getElementsByTagName("IntervalReading");
        for (int i = 0; i < intervalReadingNodes.getLength(); i++) {
            Element intervalReadingNode = (Element)intervalReadingNodes.item(i);
            XmlIntervalReading intervalReading = new XmlIntervalReading();

            NodeList valueNodes = intervalReadingNode.getElementsByTagName("value");
            if (valueNodes.getLength() == 1) {
                try {
                    intervalReading.setReading(Long.parseLong(valueNodes.item(0).getTextContent()));
                } catch (NumberFormatException nfe) {
                    throw new IllegalMeterBatchReadingXmlException("Invalid value node value : " + valueNodes.item(0).getTextContent(), nfe);
                }
            } else {
                throw new IllegalMeterBatchReadingXmlException("Invalid number of value nodes under IntervalReading node : " + valueNodes.getLength());
            }

            NodeList timePeriodNodes = intervalReadingNode.getElementsByTagName("timePeriod");
            if (timePeriodNodes.getLength() == 1) {
                Element timePeriodNode = (Element)timePeriodNodes.item(0);
                parseTimePeriodNode(intervalReading, timePeriodNode);
            } else {
                throw new IllegalMeterBatchReadingXmlException("Invalid number of timePeriod nodes under IntervalReading node : " + timePeriodNodes.getLength());
            }

            result.getIntervalReadings().add(intervalReading);
        }
    }

    private void parseTimePeriodNode(XmlIntervalReading intervalReading, Element timePeriodNode) {
        NodeList durationNodes = timePeriodNode.getElementsByTagName("duration");
        if (durationNodes.getLength() == 1) {
            try {
                intervalReading.setDurationAsSec(Long.parseLong(durationNodes.item(0).getTextContent()));
            } catch (NumberFormatException nfe) {
                throw new IllegalMeterBatchReadingXmlException("Invalid duration node value : " + durationNodes.item(0).getTextContent(), nfe);
            }
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of duration nodes under timePeriod node : " + durationNodes.getLength());
        }

        NodeList startNodes = timePeriodNode.getElementsByTagName("start");
        if (startNodes.getLength() == 1) {
            try {
                intervalReading.setStartEpochAsSec(Long.parseLong(startNodes.item(0).getTextContent()));
            } catch (NumberFormatException nfe) {
                throw new IllegalMeterBatchReadingXmlException("Invalid start node value : " + startNodes.item(0).getTextContent(), nfe);
            }
        } else {
            throw new IllegalMeterBatchReadingXmlException("Invalid number of start nodes under timePeriod node : " + startNodes.getLength());
        }
    }
}
