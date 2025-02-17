package u5w2d5.etm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.model.*;
import u5w2d5.etm.repository.*;
import u5w2d5.etm.request.EmployeeRequestDTO;
import u5w2d5.etm.response.IdResponse;
import u5w2d5.etm.response.EmployeeResponseDTO;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public List<EmployeeResponseDTO> getAllDTO() {
        List<EmployeeResponseDTO> employeeDTOs = new ArrayList<>();

        for (Employee employee : employeeRepository.findAll()) {
            EmployeeResponseDTO employeeDTO = new EmployeeResponseDTO();
            BeanUtils.copyProperties(employee, employeeDTO);

            List<Booking> employeeBookings = employee.getBookings();
            List<Long> tripIds = employeeBookings.stream()
                    .map(booking -> booking.getTrip().getId())
                    .collect(Collectors.toList());
            employeeDTO.setTripIds(tripIds);

            employeeDTOs.add(employeeDTO);
        }
        return employeeDTOs;
    }

    public Employee getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return employee;
    }

    public EmployeeResponseDTO getEmployeeByIdDTO(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        EmployeeResponseDTO employeeDTO = new EmployeeResponseDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        List<Booking> employeeBookings = employee.getBookings();
        List<Long> tripIds = employeeBookings.stream()
                .map(booking -> booking.getTrip().getId())
                .collect(Collectors.toList());

        employeeDTO.setTripIds(tripIds);
        return employeeDTO;
    }

    public IdResponse createEmployee(EmployeeRequestDTO employee) {
        Employee newEmployee = new Employee();
        BeanUtils.copyProperties(employee, newEmployee);
        return new IdResponse(employeeRepository.save(newEmployee).getId());
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);
        employee.setUsername(employeeDetails.getUsername());
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setAvatarUrl(employeeDetails.getAvatarUrl());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
}