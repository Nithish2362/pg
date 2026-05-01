package pg.pg.payment.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pg.pg.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentScheduler {

    private final PaymentService paymentService;

    // Runs at midnight on the 1st of every month
    @Scheduled(cron = "0 0 0 1 * ?")
    public void scheduleMonthlyRentGeneration() {
        log.info("Starting automatic monthly rent generation...");
        try {
            paymentService.generateMonthlyRent();
            log.info("Successfully completed automatic monthly rent generation.");
        } catch (Exception e) {
            log.error("Error during automatic monthly rent generation", e);
        }
    }
}
