package energy.leap.meterhub.service.impl;

import energy.leap.meterhub.data.entity.EnergyMeter;
import energy.leap.meterhub.data.repository.EnergyMeterRepository;
import energy.leap.meterhub.data.repository.HourlyReadingRepository;
import energy.leap.meterhub.service.MeterBatchReadingProcessorService;
import energy.leap.meterhub.service.impl.mapper.XmlMeterBatchReadingMapper;
import energy.leap.meterhub.service.impl.parser.XmlMeterBatchReading;
import energy.leap.meterhub.service.impl.parser.XmlMeterBatchReadingNormalizer;
import energy.leap.meterhub.service.impl.parser.XmlMeterBatchReadingParser;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@RequiredArgsConstructor
public class MeterBatchReadingProcessorServiceImpl implements MeterBatchReadingProcessorService {
    @NonNull
    private final EnergyMeterRepository energyMeterRepository;

    @NonNull
    private final HourlyReadingRepository hourlyReadingRepository;

    @Transactional
    private void saveReading(XmlMeterBatchReading normalizedReading) {
        EnergyMeter meter;
        Optional<EnergyMeter> meterFound = energyMeterRepository.findById(normalizedReading.getId());
        meter = meterFound.orElseGet(() -> new EnergyMeter(normalizedReading.getId(), normalizedReading.getTitle()));
        energyMeterRepository.save(meter);
        hourlyReadingRepository.saveAll(XmlMeterBatchReadingMapper.convertReadingsToHourlyReadings(normalizedReading));
    }

    @Override
    public void processBatchReading(String batchReadingXmlContent) {
        // Parse
        XmlMeterBatchReadingParser parser = new XmlMeterBatchReadingParser();
        XmlMeterBatchReading batchReading = parser.parseReadingFile(batchReadingXmlContent);
        // Normalize
        XmlMeterBatchReadingNormalizer normalizer = new XmlMeterBatchReadingNormalizer();
        XmlMeterBatchReading normalizedReading = normalizer.normalize(batchReading);
        // Save
        saveReading(normalizedReading);
    }
}
