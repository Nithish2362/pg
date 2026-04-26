package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.model.Payment;
import pg.pg.service.PaymentService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public SuccessResponse getAllPayments() {
        return new SuccessResponse("Payments fetched successfully", paymentService.getAllPayments());
    }

    @GetMapping("/tenant/{tenantId}")
    public SuccessResponse getPaymentsByTenant(@PathVariable Long tenantId) {
        return new SuccessResponse("Payments fetched successfully", paymentService.getPaymentsByTenant(tenantId));
    }

    @PostMapping
    public SuccessResponse createPayment(@RequestBody Payment payment, @RequestParam Long tenantId) {
        return new SuccessResponse("Payment saved successfully", paymentService.createPayment(payment, tenantId));
    }

    @PutMapping("/{id}")
    public SuccessResponse updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        return new SuccessResponse("Payment updated successfully", paymentService.updatePayment(id, payment));
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return new SuccessResponse("Payment deleted successfully", null);
    }
}
