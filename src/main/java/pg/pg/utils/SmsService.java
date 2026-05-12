package pg.pg.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

@Service
public class SmsService {

    @Value("${app.sms.api-key:YOUR_FAST2SMS_KEY}")
    private String apiKey;

    public void sendOtp(String mobileNumber, String otp) {
        CompletableFuture.runAsync(() -> {
            // Backup logging to a file for easy debugging
            try (FileWriter writer = new FileWriter("otp_logs.txt", true)) {
                writer.write("[" + java.time.LocalDateTime.now() + "] OTP for " + mobileNumber + " is: " + otp + "\n");
            } catch (Exception e) {
                System.err.println("Could not write to otp_logs.txt");
            }

            try {
                // Ensure number is 10 digits for Fast2SMS
                String cleanNumber = mobileNumber.replaceAll("[^0-9]", "");
                if (cleanNumber.length() > 10) cleanNumber = cleanNumber.substring(cleanNumber.length() - 10);
                
                String urlString = "https://www.fast2sms.com/dev/bulkV2?authorization=" + apiKey + 
                                 "&route=otp&variables_values=" + otp + 
                                 "&numbers=" + cleanNumber;
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == 200) {
                    System.out.println("OTP sent via Fast2SMS to " + cleanNumber);
                } else {
                    Scanner s = new Scanner(conn.getErrorStream()).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    System.err.println("Fast2SMS Error (Code " + responseCode + "): " + result);
                    System.err.println("Note: Please ensure YOUR_FAST2SMS_KEY is updated in application.properties");
                }
            } catch (Exception e) {
                System.err.println("Network error sending SMS: " + e.getMessage());
            }
        });
    }
}
