package u5w2d5.etm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import u5w2d5.etm.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}