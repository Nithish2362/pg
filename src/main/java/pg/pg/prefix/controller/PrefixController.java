package pg.pg.prefix.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.prefix.model.Prefix;
import pg.pg.prefix.service.PrefixService;

import java.util.List;
@RestController
@RequestMapping(value = "/prefix")
@RequiredArgsConstructor
public class PrefixController {
    private final PrefixService prefixService;
@PostMapping("/add")
public SuccessResponse createPrefix(@RequestBody Prefix prefix) {
    return new SuccessResponse("Prefix Submitted", prefixService.createNew(prefix));
}

@GetMapping("/get-all-prefix")
public List<Prefix> getAllPrefixes() {
    return prefixService.getAll();
}
}