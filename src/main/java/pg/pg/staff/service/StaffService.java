package pg.pg.staff.service;

import org.springframework.data.domain.Page;
import pg.pg.staff.dto.StaffDto;

import java.util.Map;

public interface StaffService {
    Map<String, Object> getAllStaff(int page, int pageSize, String searchTerm, String locationId, String buildingId);
    StaffDto createStaff(StaffDto staffDto);
    StaffDto updateStaff(String id, StaffDto staffDto);
    void deleteStaff(String id);
    StaffDto getStaffById(String id);
}
