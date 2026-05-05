package pg.pg.expense.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.Exception.InvalidDataException;
import pg.pg.building.model.Building;
import pg.pg.building.repository.BuildingRepository;
import pg.pg.expense.dto.ExpenseDto;
import pg.pg.expense.model.Expense;
import pg.pg.expense.repository.ExpenseRepository;
import pg.pg.location.model.Location;
import pg.pg.location.repository.LocationRepository;
import pg.pg.staff.repository.StaffRepository;
import pg.pg.utils.SecurityUtils;

import pg.pg.staff.model.Staff;
import pg.pg.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BuildingRepository buildingRepository;
    private final LocationRepository locationRepository;
    private final StaffRepository staffRepository;
    private final SecurityUtils securityUtils;

    public Map<String, Object> getAllExpenses(int page, int pageSize, String searchTerm, String buildingId) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : buildingId;
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Expense> expensePage = expenseRepository.findBySearchTermAndBuilding(searchTerm, effectiveBuildingId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("response", expensePage.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        response.put("count", expensePage.getTotalElements());
        return response;
    }


    public ExpenseDto createExpense(ExpenseDto dto) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        
        Location location = null;
        if (StringUtils.hasText(dto.getLocationId())) {
            location = locationRepository.findByLocationId(dto.getLocationId())
                    .orElseThrow(() -> new InvalidDataException("Location not found"));
        }

        Building building = null;
        // If staff is creating, force their building
        if (staffBuildingId != null) {
            building = buildingRepository.findByBuildingId(staffBuildingId)
                    .orElseThrow(() -> new InvalidDataException("Your building was not found"));
            location = building.getLocation();
        } else if (StringUtils.hasText(dto.getBuildingId())) {
            building = buildingRepository.findByBuildingId(dto.getBuildingId())
                    .orElseThrow(() -> new InvalidDataException("Building not found"));
        }

        Staff staff = securityUtils.getCurrentStaff().orElse(null);
        User currentUser = securityUtils.getCurrentUser().orElse(null);

        Expense expense = Expense.builder()
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .expenseDate(dto.getExpenseDate())
                .category(dto.getCategory())
                .remarks(dto.getRemarks())
                .location(location)
                .building(building)
                .staff(staff)
                .build();

        if (currentUser != null) {
            expense.setCreatedBy(currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername());
        } else {
            expense.setCreatedBy("System");
        }

        return toDto(expenseRepository.save(expense));
    }

    public ExpenseDto updateExpense(String id, ExpenseDto dto) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Expense not found"));

        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        if (staffBuildingId != null && existing.getBuilding() != null && 
            !staffBuildingId.equals(existing.getBuilding().getBuildingId())) {
            throw new RuntimeException("Access Denied: You cannot update expenses for other buildings.");
        }

        existing.setTitle(dto.getTitle());
        existing.setAmount(dto.getAmount());
        existing.setExpenseDate(dto.getExpenseDate());
        existing.setCategory(dto.getCategory());
        existing.setRemarks(dto.getRemarks());

        if (staffBuildingId == null) { // Only Admin can change the building/location of an expense
            if (StringUtils.hasText(dto.getLocationId())) {
                Location location = locationRepository.findByLocationId(dto.getLocationId())
                        .orElseThrow(() -> new InvalidDataException("Location not found"));
                existing.setLocation(location);
            } else {
                existing.setLocation(null);
            }

            if (StringUtils.hasText(dto.getBuildingId())) {
                Building building = buildingRepository.findByBuildingId(dto.getBuildingId())
                        .orElseThrow(() -> new InvalidDataException("Building not found"));
                existing.setBuilding(building);
            } else {
                existing.setBuilding(null);
            }
        }

        return toDto(expenseRepository.save(existing));
    }

    public void deleteExpense(String id) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Expense not found"));

        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        if (staffBuildingId != null && existing.getBuilding() != null && 
            !staffBuildingId.equals(existing.getBuilding().getBuildingId())) {
            throw new RuntimeException("Access Denied.");
        }
        
        expenseRepository.deleteById(id);
    }

    private ExpenseDto toDto(Expense e) {
        boolean isOldStaff = false;
        String finalStaffName = e.getCreatedBy() != null ? e.getCreatedBy() : "Admin";

        if (e.getStaff() != null && e.getBuilding() != null) {
            Building currentStaffBuilding = e.getStaff().getBuilding();
            if (currentStaffBuilding == null || !currentStaffBuilding.getBuildingId().equals(e.getBuilding().getBuildingId())) {
                isOldStaff = true;
            }
            // If the staff name was overwritten in the DB
            if (e.getCreatedBy() != null && !e.getCreatedBy().equals(e.getStaff().getName())) {
                isOldStaff = true;
            }
        }

        return ExpenseDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .category(e.getCategory())
                .remarks(e.getRemarks())
                .locationId(e.getLocation() != null ? e.getLocation().getLocationId() : null)
                .locationName(e.getLocation() != null ? e.getLocation().getLocationName() : null)
                .buildingId(e.getBuilding() != null ? e.getBuilding().getBuildingId() : null)
                .buildingName(e.getBuilding() != null ? e.getBuilding().getBuildingName() : null)
                .staffName(finalStaffName)
                .staffNumber(e.getStaff() != null ? e.getStaff().getStaffNumber() : "ADMIN")
                .createdDate(e.getCreatedDate())
                .isOldStaff(isOldStaff)
                .build();
    }
}
