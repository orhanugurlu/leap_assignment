package energy.leap.meterhub.web;

import energy.leap.meterhub.service.MeterReadingReportService;
import energy.leap.meterhub.service.dto.EnergyMeterDto;
import energy.leap.meterhub.service.dto.HourlyReportDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class MeterReadingReportController {
    @NonNull
    private final MeterReadingReportService service;

    @GetMapping("/meters")
    public List<EnergyMeterDto> getEnergyMeters() {
        return service.getEnergyMeters();
    }

    @GetMapping("/hourly_report/{id}")
    public List<HourlyReportDto> getHourlyReportForMeter(@PathVariable("id") String id) {
        return service.getHourlyReportsForMeter(id);
    }

    @GetMapping("/total_reading/{id}")
    public Long getTotalReadingAsWhForMeter(@PathVariable("id") String id) {
        return service.getTotalReadingAsWhForMeter(id);
    }

    @GetMapping("/total_cost/{id}")
    public Double getTotalCostForMeter(@PathVariable("id") String id) {
        return service.getTotalCostForMeter(id);
    }
}
