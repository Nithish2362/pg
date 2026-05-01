package pg.pg.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.location.Dto.LocationDto;
import pg.pg.location.service.LocationService;
import pg.pg.utils.Types;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/locations")   // Cleaner mapping
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public SuccessResponse createLocation(@RequestBody LocationDto locationDto) {
        return new SuccessResponse("Location Created Successfully",
                locationService.createLocation(locationDto));
    }

    @GetMapping
    public SuccessResponse getAllLocations() {
        return new SuccessResponse("Locations Fetched Successfully",
                locationService.getAllLocations());
    }

    @GetMapping("/get-all")
    public SuccessResponse getAllLocationsOld() {
        return new SuccessResponse("Locations Fetched Successfully",
                locationService.getAllLocations());
    }

    @GetMapping("/view")
    public SuccessResponse getAllPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "ACTIVE") Types.Status status) {

        org.springframework.data.domain.Page<LocationDto> result = locationService.getAllPaginatedLocations(searchTerm, status, page, pageSize);
        return new SuccessResponse("Locations Fetched Successfully", result.getContent(), result.getTotalElements());
    }

    @PutMapping("/{locationId}")
    public SuccessResponse updateLocation(@PathVariable("locationId") String locationId, @RequestBody LocationDto locationDto) {
        return new SuccessResponse("Location Updated Successfully",
                locationService.updateLocation(locationId, locationDto));
    }

    @DeleteMapping("/{locationId}")
    public SuccessResponse deleteLocation(@PathVariable("locationId") String locationId) {
        locationService.deleteLocation(locationId);
        return new SuccessResponse("Location Deleted Successfully", null);
    }
}