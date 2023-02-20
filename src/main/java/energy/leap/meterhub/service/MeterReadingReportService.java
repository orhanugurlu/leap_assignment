package energy.leap.meterhub.service;

import energy.leap.meterhub.service.dto.EnergyMeterDto;
import energy.leap.meterhub.service.dto.HourlyReportDto;

import java.util.List;

public interface MeterReadingReportService {
    List<EnergyMeterDto> getEnergyMeters();
    List<HourlyReportDto> getHourlyReportsForMeter(String meterId);
    Long getTotalReadingAsWhForMeter(String meterId);
    Double getTotalCostForMeter(String meterId);
}
