// ===============================================
// PaymentController.java
// ===============================================
package pg.pg.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.payment.dto.PaymentDto;
import pg.pg.payment.service.PaymentService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public SuccessResponse getAllPayments() {
        return new SuccessResponse(
                "Payments fetched successfully",
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/view")
    public SuccessResponse getPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status) {

        org.springframework.data.domain.Page<PaymentDto> result = paymentService.getAllPaginatedPayments(
                searchTerm, status, page, pageSize);
        return new SuccessResponse(
                "Payments Fetched Successfully",
                result.getContent(),
                result.getTotalElements()
        );
    }

    @GetMapping("/counts")
    public SuccessResponse getCounts() {
        return new SuccessResponse("Counts fetched", paymentService.getCounts());
    }

    @PostMapping("/generate-rent")
    public SuccessResponse generateMonthlyRent() {
        paymentService.generateMonthlyRent();
        return new SuccessResponse(
                "Monthly rent generated successfully for all active tenants",
                null
        );
    }

    @GetMapping("/tenant/{tenantId}")
    public SuccessResponse getPaymentsByTenant(@PathVariable String tenantId) {
        return new SuccessResponse(
                "Payments fetched successfully",
                paymentService.getPaymentsByTenant(tenantId)
        );
    }

    @GetMapping("/{id}")
    public SuccessResponse getPaymentById(@PathVariable Long id) {
        return new SuccessResponse(
                "Payment fetched successfully",
                paymentService.getPaymentById(id)
        );
    }

    @PostMapping
    public SuccessResponse createPayment(
            @RequestBody PaymentDto dto,
            @RequestParam String tenantId
    ) {
        return new SuccessResponse(
                "Payment saved successfully",
                paymentService.createPayment(dto, tenantId)
        );
    }

    @PutMapping("/{id}")
    public SuccessResponse updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentDto dto
    ) {
        return new SuccessResponse(
                "Payment updated successfully",
                paymentService.updatePayment(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);

        return new SuccessResponse(
                "Payment deleted successfully",
                null
        );
    }
}