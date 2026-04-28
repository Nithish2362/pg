package pg.pg.bed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.bed.Dto.BedDto;
import pg.pg.bed.service.BedService;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.utils.Types;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/beds")
public class BedController {

    private final BedService bedService;

    @PostMapping
    public SuccessResponse createBed(@RequestBody BedDto bedDto) {
        return new SuccessResponse("Bed Created Successfully",
                bedService.createBed(bedDto));
    }

    @GetMapping
    public SuccessResponse getAllBeds() {
        return new SuccessResponse("Beds Fetched Successfully",
                bedService.getAllBeds());
    }

    @GetMapping("/get-all")
    public SuccessResponse getAllBedsOld() {
        return new SuccessResponse("Beds Fetched Successfully",
                bedService.getAllBeds());
    }

    @GetMapping("/view")
    public SuccessResponse getAllPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "ACTIVE") Types.Status status) {

        return new SuccessResponse("Beds Fetched Successfully",
                bedService.getAllPaginatedBeds(searchTerm, status, page, pageSize));
    }
}
