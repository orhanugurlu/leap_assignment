package energy.leap.meterhub.data.repository;

import energy.leap.meterhub.data.entity.HourlyReading;
import energy.leap.meterhub.data.entity.HourlyReadingKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HourlyReadingRepository extends JpaRepository<HourlyReading, HourlyReadingKey> {
    List<HourlyReading> findByIdMeterId(String meterId);

    @Query("SELECT SUM(hr.readingAsWh) FROM HourlyReading hr WHERE hr.id.meterId=:meterId")
    Long getTotalReadingAsWhOfMeter(@Param("meterId") String meterId);

    @Query("SELECT SUM(hr.readingAsWh * hr.pricePerKwh / 1000) FROM HourlyReading hr WHERE hr.id.meterId=:meterId")
    Double getTotalCostOfMeter(@Param("meterId") String meterId);
}
