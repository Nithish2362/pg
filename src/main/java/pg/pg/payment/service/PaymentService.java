// ===============================================
// PaymentService.java
// ===============================================
package pg.pg.payment.service;

import pg.pg.payment.dto.PaymentDto;

import java.util.List;

public interface PaymentService {

    List<PaymentDto> getAllPayments();

    List<PaymentDto> getPaymentsByTenant(String tenantId);

    PaymentDto getPaymentById(Long id);

    PaymentDto createPayment(PaymentDto dto, String tenantId);

    PaymentDto updatePayment(Long id, PaymentDto dto);

    void deletePayment(Long id);
}