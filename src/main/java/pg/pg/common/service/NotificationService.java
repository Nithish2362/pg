package pg.pg.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pg.pg.common.dto.Msg91Request;
import pg.pg.tenant.model.Tenant;
import pg.pg.utils.EmailService;

import java.time.LocalDateTime;

@Service
@Slf4j
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final String mgAuth = "449282AooLe2c0Ejmu68148fd2P1";

    public void sendSms(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return;

        try {
            String mobileNo = phoneNumber.trim();
            if (mobileNo.length() > 10) {
                mobileNo = mobileNo.substring(mobileNo.length() - 10);
            }

            Msg91Request request = new Msg91Request();
            request.setFlow_id("6858fd4bd6fc0546ee2fa752"); // Default flow from ultronb2b
            request.setSender("UTRTEX");
            request.setMobiles("91" + mobileNo);
            
            // Using message as VAR1 for the flow template
            request.setVAR1(message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authkey", mgAuth);

            HttpEntity<Msg91Request> entity = new HttpEntity<>(request, headers);

            log.info("Sending real SMS via MSG91 to: {}", "91" + mobileNo);
            String response = restTemplate.postForObject("https://api.msg91.com/api/v5/flow/", entity, String.class);
            log.info("Response from MSG91: {}", response);
        } catch (Exception e) {
            log.error("Error sending SMS via MSG91: {}", e.getMessage());
        }
    }

    public void sendEmail(String email, String subject, String message) {
        if (email == null || email.isEmpty()) return;
        log.info("Sending real email to: {}", email);
        emailService.sendNotification(email, subject, message);
    }

    public void sendToParents(Tenant tenant, String message, String subject) {
        log.info("Initiating Notification to Parents of Tenant: {}", tenant.getStudentName());
        
        // SMS (Mock)
        sendSms(tenant.getFatherMobile(), message);
        sendSms(tenant.getMotherMobile(), message);
        sendSms(tenant.getGuardianMobile(), message);

        // Emails (Real)
        if (tenant.getFatherEmail() != null && !tenant.getFatherEmail().isEmpty()) {
            sendEmail(tenant.getFatherEmail(), subject, message);
        }
        if (tenant.getMotherEmail() != null && !tenant.getMotherEmail().isEmpty()) {
            sendEmail(tenant.getMotherEmail(), subject, message);
        }
        if (tenant.getGuardianEmail() != null && !tenant.getGuardianEmail().isEmpty()) {
            sendEmail(tenant.getGuardianEmail(), subject, message);
        }

        // Also notify the tenant's primary email just in case
        if (tenant.getEmail() != null) {
            sendEmail(tenant.getEmail(), subject, message);
        }
    }
    
    public void sendToAllParents(Iterable<Tenant> tenants, String message, String subject) {
        for (Tenant tenant : tenants) {
            sendToParents(tenant, message, subject);
        }
    }
}
