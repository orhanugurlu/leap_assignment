package energy.leap.meterhub.service.impl.mapper;

import energy.leap.meterhub.data.entity.EnergyMeter;
import energy.leap.meterhub.service.dto.EnergyMeterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnergyMeterMapper {
    EnergyMeterDto mapToEnergyMeterServiceDto(EnergyMeter meter);
}
