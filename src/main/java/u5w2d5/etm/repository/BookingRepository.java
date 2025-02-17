package u5w2d5.etm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import u5w2d5.etm.model.Booking;
import u5w2d5.etm.model.Employee;
import u5w2d5.etm.model.Trip;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    public List<Booking> findByEmployee(Employee employee);

    public List<Booking> findByEmployeeId(long employeeId);

    public boolean existsByEmployeeAndTrip(Employee employee, Trip trip);
}