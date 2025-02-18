package u5w2d5.etm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.auth.model.AppUser;
import u5w2d5.etm.auth.model.AppUserRole;
import u5w2d5.etm.auth.request.AppUserRegistrationRequest;
import u5w2d5.etm.response.IdResponse;
import u5w2d5.etm.auth.service.AppUserService;
import u5w2d5.etm.model.Employee;
import u5w2d5.etm.response.EmployeeResponseDTO;
import u5w2d5.etm.service.EmployeeService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AppUserService appUserService;

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
    // @PreAuthorize("hasRole('ADMIN')")
    // public IdResponse createEmployee(@RequestBody EmployeeRequestDTO employee) {
    // return employeeService.createEmployee(employee);
    // }
    public IdResponse register(@Valid @RequestBody AppUserRegistrationRequest registerRequest) {
        AppUser appUser = appUserService.registerUser(
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                Set.of(registerRequest.getRole() == null ? AppUserRole.ROLE_USER
                        : AppUserRole.valueOf("ROLE_" + registerRequest.getRole())));
        return new IdResponse(appUser.getId());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        return updatedEmployee;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(@PathVariable Long id) throws MessagingException, Exception {
        employeeService.deleteEmployee(id);
    }
}