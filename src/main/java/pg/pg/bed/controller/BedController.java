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

    @GetMapping("/{bedId}")
    public SuccessResponse getBedById(@PathVariable String bedId) {
        return new SuccessResponse("Bed Fetched Successfully",
                bedService.getBedById(bedId));
    }

    @PutMapping("/{bedId}")
    public SuccessResponse updateBed(@PathVariable String bedId, @RequestBody BedDto bedDto) {
        return new SuccessResponse("Bed Updated Successfully",
                bedService.updateBed(bedId, bedDto));
    }

    @DeleteMapping("/{bedId}")
    public SuccessResponse deleteBed(@PathVariable String bedId) {
        bedService.deleteBed(bedId);
        return new SuccessResponse("Bed Deleted Successfully", null);
    }
}
