package pg.pg.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.location.Dto.LocationDto;
import pg.pg.location.service.LocationService;
import pg.pg.utils.Types;

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

    @GetMapping("/get-all")
    public SuccessResponse getAllLocations() {
        return new SuccessResponse("Locations Fetched Successfully",
                locationService.getAllLocations());
    }

    @GetMapping("/view")
    public SuccessResponse getAllPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "ACTIVE") Types.Status status) {

        return new SuccessResponse("Locations Fetched Successfully",
                locationService.getAllPaginatedLocations(searchTerm, status, page, pageSize));
    }
}