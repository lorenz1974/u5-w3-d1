package u5w2d5.etm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import u5w2d5.etm.model.Booking;
import u5w2d5.etm.request.BookingRequestDTO;
import u5w2d5.etm.response.BookingResponseDTO;
import u5w2d5.etm.response.IdResponse;
import u5w2d5.etm.service.BookingService;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDTO> getAllDTO() {
        return bookingService.getAllBookingsDTO();
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getBookingByIdDTO(@PathVariable Long id) {
        return bookingService.getBookingByIdDTO(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse createBooking(@RequestBody BookingRequestDTO booking) {
        return bookingService.createBooking(booking);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Booking updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        Booking updatedBooking = bookingService.updateBooking(id, bookingDetails);
        return updatedBooking;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
