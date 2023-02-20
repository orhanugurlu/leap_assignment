package energy.leap.meterhub.data.repository;

import energy.leap.meterhub.data.entity.EnergyMeter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnergyMeterRepository extends JpaRepository<EnergyMeter, String> {
}