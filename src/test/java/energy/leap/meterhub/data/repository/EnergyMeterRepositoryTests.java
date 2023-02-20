package energy.leap.meterhub.data.repository;

import energy.leap.meterhub.data.entity.EnergyMeter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EnergyMeterRepositoryTests {

    @Autowired
    EnergyMeterRepository energyMeterRepository;

    @Test
    void GivenConfigurationOk_WhenContextLoaded_ThenEnergyMeterRepositoryIsAvailable() {
        // Arrange
        // Configuration is already the code itself
        // Act
        // DataJpaTest loads a partial context including repository
        // Assert
        Assertions.assertNotNull(energyMeterRepository);
    }

    @Test
    void GivenConfigurationOk_WhenAllEnergyMetersQueried_ThenNoEnergyMetersFound() {
        // Arrange
        // Configuration is already the code itself
        // Act
        List<EnergyMeter> energyMeters = energyMeterRepository.findAll();
        // Assert
        assertThat(energyMeters).isEmpty();
    }

    @Test
    void GivenAnEnergyMeterIsSaved_WhenAllEnergyMetersQueried_ThenSavedEnergyMeterIsFound() {
        // Arrange
        EnergyMeter energyMeter = new EnergyMeter("dummy_id", "dummy title");
        energyMeter = energyMeterRepository.save(energyMeter);
        // Act
        List<EnergyMeter> energyMeters = energyMeterRepository.findAll();
        // Assert
        assertThat(energyMeters).containsOnly(energyMeter);
    }

    @Test
    void GivenTwoEnergyMetersAreSaved_WhenAllEnergyMetersQueried_ThenSavedEnergyMetersAreFound() {
        // Arrange
        EnergyMeter energyMeter1 = new EnergyMeter("dummy_id_1", "dummy title 1");
        energyMeter1 = energyMeterRepository.save(energyMeter1);
        EnergyMeter energyMeter2 = new EnergyMeter("dummy_id_2", "dummy title 2");
        energyMeter2 = energyMeterRepository.save(energyMeter2);
        // Act
        List<EnergyMeter> energyMeters = energyMeterRepository.findAll();
        // Assert
        assertThat(energyMeters).containsOnly(energyMeter1, energyMeter2);
    }

    @Test
    void GivenOneEnergyMeterIsSaved_WhenEnergyMeterWithSameIdSaved_ThenSavedEnergyIsUpdated() {
        // Arrange
        energyMeterRepository.save(new EnergyMeter("dummy_id_1", "dummy title 1"));
        // Act
        EnergyMeter energyMeter2 = new EnergyMeter("dummy_id_1", "dummy title 2");
        energyMeter2 = energyMeterRepository.save(energyMeter2);
        // Assert
        List<EnergyMeter> energyMeters = energyMeterRepository.findAll();
        assertThat(energyMeters).containsOnly(energyMeter2);
    }

}
