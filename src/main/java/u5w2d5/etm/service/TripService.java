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
import u5w2d5.etm.request.TripRequestDTO;
import u5w2d5.etm.response.IdResponse;
import u5w2d5.etm.response.TripResponseDTO;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class TripService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public List<Trip> getAll() {
        return tripRepository.findAll();
    }

    public List<TripResponseDTO> getAllTripsDTO() {
        List<TripResponseDTO> tripDTOs = new ArrayList<>();

        for (Trip trip : tripRepository.findAll()) {
            TripResponseDTO tripDTO = new TripResponseDTO();
            BeanUtils.copyProperties(trip, tripDTO);

            List<Booking> tripBookings = trip.getBookings();
            List<Long> employeeIds = tripBookings.stream()
                    .map(booking -> booking.getEmployee().getId())
                    .collect(Collectors.toList());
            tripDTO.setEmployeeIds(employeeIds);

            tripDTOs.add(tripDTO);
        }
        return tripDTOs;
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + id));
    }

    public TripResponseDTO getTripByIdDTO(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + id));

        TripResponseDTO tripDTO = new TripResponseDTO();
        BeanUtils.copyProperties(trip, tripDTO);

        List<Booking> tripBookings = trip.getBookings();
        List<Long> employeeIds = tripBookings.stream()
                .map(booking -> booking.getEmployee().getId())
                .collect(Collectors.toList());

        tripDTO.setEmployeeIds(employeeIds);
        return tripDTO;
    }

    public IdResponse createTrip(TripRequestDTO trip) {
        if (trip.getStartDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Trip newTrip = new Trip();
        BeanUtils.copyProperties(trip, newTrip);
        return new IdResponse(tripRepository.save(newTrip).getId());
    }

    public Trip updateTrip(Long id, Trip tripDetails) {
        if (tripDetails.getStartDate().isAfter(tripDetails.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Trip trip = getTripById(id);
        trip.setDescription(tripDetails.getDescription());
        trip.setStartDate(tripDetails.getStartDate());
        trip.setEndDate(tripDetails.getEndDate());
        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        Trip trip = getTripById(id);
        tripRepository.delete(trip);
    }

    public List<Trip> getEmployeeTrips(long employeeId) {

        List<Booking> bookings = bookingRepository.findByEmployeeId(employeeId);
        return bookings.stream().map(Booking::getTrip).toList();

    }
}