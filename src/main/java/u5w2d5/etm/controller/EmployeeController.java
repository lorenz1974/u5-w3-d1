package u5w2d5.etm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.model.Employee;
import u5w2d5.etm.request.EmployeeRequestDTO;
import u5w2d5.etm.response.IdResponse;
import u5w2d5.etm.response.EmployeeResponseDTO;
import u5w2d5.etm.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    // public List<Employee> getAll() {
    // return employeeService.getAll();
    // }
    public List<EmployeeResponseDTO> getAllDTO() {
        return employeeService.getAllDTO();
    }

    @GetMapping("/{id}")
    public EmployeeResponseDTO getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeByIdDTO(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse createEmployee(@RequestBody EmployeeRequestDTO employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        return updatedEmployee;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) throws MessagingException, Exception {
        employeeService.deleteEmployee(id);
    }
}