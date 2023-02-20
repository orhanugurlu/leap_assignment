package energy.leap.meterhub.service.impl;

import energy.leap.meterhub.data.repository.EnergyMeterRepository;
import energy.leap.meterhub.data.repository.HourlyReadingRepository;
import energy.leap.meterhub.service.MeterReadingReportService;
import energy.leap.meterhub.service.dto.EnergyMeterDto;
import energy.leap.meterhub.service.dto.HourlyReportDto;
import energy.leap.meterhub.service.exception.MeterNotFoundException;
import energy.leap.meterhub.service.impl.mapper.EnergyMeterMapper;
import energy.leap.meterhub.service.impl.mapper.HourlyReadingMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MeterReadingReportServiceImpl implements MeterReadingReportService {

    @NonNull
    private final EnergyMeterRepository energyMeterRepository;

    @NonNull
    private final HourlyReadingRepository hourlyReadingRepository;

    @NonNull
    private final EnergyMeterMapper energyMeterMapper;

    @NonNull
    private final HourlyReadingMapper hourlyReadingMapper;

    @Override
    public List<EnergyMeterDto> getEnergyMeters() {
        return energyMeterRepository
                .findAll()
                .stream()
                .map(energyMeterMapper::mapToEnergyMeterServiceDto)
                .toList();
    }

    @Override
    public List<HourlyReportDto> getHourlyReportsForMeter(String meterId) {
        return hourlyReadingRepository
                .findByIdMeterId(meterId)
                .stream()
                .map(hourlyReadingMapper::mapToHourlyReportDto)
                .toList();
    }

    @Override
    public Long getTotalReadingAsWhForMeter(String meterId) {
        return Optional.ofNullable(hourlyReadingRepository.getTotalReadingAsWhOfMeter(meterId))
                .orElseThrow(() -> new MeterNotFoundException(String.format("Total reading requested for non-existing meter : %s", meterId)));
    }

    @Override
    public Double getTotalCostForMeter(String meterId) {
        return Optional.ofNullable(hourlyReadingRepository.getTotalCostOfMeter(meterId))
                .orElseThrow(() -> new MeterNotFoundException(String.format("Total cost requested for non-existing meter : %s", meterId)));
    }
}
