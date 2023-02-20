package energy.leap.meterhub.service;

public interface MeterBatchReadingProcessorService {
    void processBatchReading(String batchReadingXmlContent);
}
