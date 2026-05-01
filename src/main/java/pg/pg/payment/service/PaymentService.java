// ===============================================
// PaymentService.java
// ===============================================
package pg.pg.payment.service;

import pg.pg.payment.dto.PaymentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PaymentService {

    List<PaymentDto> getAllPayments();

    List<PaymentDto> getPaymentsByTenant(String tenantId);

    PaymentDto getPaymentById(Long id);

    PaymentDto createPayment(PaymentDto dto, String tenantId);

    PaymentDto updatePayment(Long id, PaymentDto dto);

    void deletePayment(Long id);

    void generateMonthlyRent();

    Page<PaymentDto> getAllPaginatedPayments(String searchTerm, String status, int page, int pageSize);

    Map<String, Long> getCounts();
}