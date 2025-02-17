package u5w2d5.etm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.model.Trip;
import u5w2d5.etm.request.TripRequestDTO;
import u5w2d5.etm.response.IdResponse;
import u5w2d5.etm.response.TripResponseDTO;
import u5w2d5.etm.service.TripService;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public List<TripResponseDTO> getAllDTO() {
        return tripService.getAllTripsDTO();
    }

    @GetMapping("/{id}")
    public TripResponseDTO getTripById(@PathVariable Long id) {
        return tripService.getTripByIdDTO(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse createTrip(@RequestBody TripRequestDTO trip) {
        return tripService.createTrip(trip);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Trip updateTrip(@PathVariable Long id, @RequestBody Trip tripDetails) {
        Trip updatedTrip = tripService.updateTrip(id, tripDetails);
        return updatedTrip;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}