package u5w2d5.etm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.model.Booking;
import u5w2d5.etm.model.Employee;
import u5w2d5.etm.model.Trip;
import u5w2d5.etm.repository.*;
import u5w2d5.etm.request.BookingRequestDTO;
import u5w2d5.etm.response.BookingResponseDTO;
import u5w2d5.etm.response.IdResponse;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmployeeService employeeService;
    private final TripService tripService;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<BookingResponseDTO> getAllBookingsDTO() {
        List<BookingResponseDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : bookingRepository.findAll()) {
            BookingResponseDTO bookingDTO = new BookingResponseDTO();
            BeanUtils.copyProperties(booking, bookingDTO);
            bookingDTOs.add(bookingDTO);
        }
        return bookingDTOs;
    }

    public Booking getBookingById(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
    }

    public BookingResponseDTO getBookingByIdDTO(long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        BookingResponseDTO bookingDTO = new BookingResponseDTO();
        BeanUtils.copyProperties(booking, bookingDTO);
        return bookingDTO;
    }

    public IdResponse createBooking(BookingRequestDTO bookingRequestDTO) {

        Employee employee = employeeService.getEmployeeById(bookingRequestDTO.getEmployeeId());
        Trip trip = tripService.getTripById(bookingRequestDTO.getTripId());

        if (bookingRepository.existsByEmployeeAndTrip(employee, trip)) {
            throw new IllegalArgumentException("The employee has already booked this trip.");
        }

        Booking booking = new Booking();
        BeanUtils.copyProperties(bookingRequestDTO, booking);
        booking.setEmployee(employee);
        booking.setTrip(trip);
        return new IdResponse(bookingRepository.save(booking).getId());
    }

    public IdResponse createBooking(Booking booking) {

        if (bookingRepository.existsByEmployeeAndTrip(booking.getEmployee(), booking.getTrip())) {
            throw new IllegalArgumentException("The employee has already booked this trip.");
        }
        return new IdResponse(bookingRepository.save(booking).getId());
    }

    public Booking updateBooking(long id, Booking updatedBooking) {
        Booking booking = getBookingById(id);
        booking.setTrip(updatedBooking.getTrip());
        booking.setEmployee(updatedBooking.getEmployee());
        booking.setRequestDate(updatedBooking.getRequestDate());
        return bookingRepository.save(booking);

    }

    public void deleteBooking(long id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);
    }
}
