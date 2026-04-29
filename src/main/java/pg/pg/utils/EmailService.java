package pg.pg.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    private void sendEmail(String to, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Happy Stay Management"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendCredentials(String to, String username, String password) {
        String subject = "Welcome to Happy Stay - Your Credentials";
        String body = "<html><body>" +
                "<h3>Welcome to Happy Stay!</h3>" +
                "<p>Your account has been created successfully.</p>" +
                "<p><b>PG ID / Username:</b> " + username + "</p>" +
                "<p><b>Temporary Password (OTP):</b> <span style='font-size: 18px; color: #4f46e5;'>" + password + "</span></p>" +
                "<p>Please login to the portal and change your password immediately.</p>" +
                "<br><p>Best regards,<br>Happy Stay Management</p>" +
                "</body></html>";
        
        CompletableFuture.runAsync(() -> sendEmail(to, subject, body));
    }

    public void sendOtp(String to, String otp) {
        String subject = "Happy Stay - Password Reset OTP";
        String body = "<html><body>" +
                "<h3>Password Reset Request</h3>" +
                "<p>Hello,</p>" +
                "<p>Your OTP for password reset is: <span style='font-size: 20px; font-weight: bold; color: #4f46e5;'>" + otp + "</span></p>" +
                "<p>This OTP is valid for 10 minutes.</p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<br><p>Best regards,<br>Happy Stay Management</p>" +
                "</body></html>";

        CompletableFuture.runAsync(() -> sendEmail(to, subject, body));
    }
}
