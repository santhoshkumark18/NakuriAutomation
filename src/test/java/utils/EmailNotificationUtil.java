package utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotificationUtil {
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    public static void sendExecutionReport(String toEmail, String subject, String executionSummary, boolean isSuccess) {
        String fromEmail = System.getenv("SENDER_EMAIL");
        String appPassword = System.getenv("SENDER_APP_PASSWORD");
        
        if (fromEmail == null || appPassword == null) {
            System.out.println("Email credentials not found. Skipping email notification.");
            return;
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            
            String htmlContent = generateEmailContent(executionSummary, isSuccess);
            message.setContent(htmlContent, "text/html");
            
            Transport.send(message);
            System.out.println("Email notification sent successfully to: " + toEmail);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateEmailContent(String executionSummary, boolean isSuccess) {
        String statusColor = isSuccess ? "#28a745" : "#dc3545";
        String statusIcon = isSuccess ? "[SUCCESS]" : "[FAILED]";
        String statusText = isSuccess ? "SUCCESS" : "FAILED";
        String currentTime = java.time.LocalDateTime.now().toString();
        String statusClass = isSuccess ? "success" : "failed";
        String nextSteps = isSuccess ? 
            "<li>Your Naukri profile has been successfully updated</li><li>Profile will show 'Updated Today' status</li><li>Increased visibility to recruiters</li>" :
            "<li>Check the execution logs for detailed error information</li><li>Verify your Naukri credentials</li><li>Ensure resume file is accessible</li><li>Manual profile update may be needed</li>";
        
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 20px; }" +
                    ".header { background-color: " + statusColor + "; color: white; padding: 15px; border-radius: 5px; }" +
                    ".content { padding: 20px; border: 1px solid #ddd; border-radius: 5px; margin-top: 10px; }" +
                    ".summary { background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0; }" +
                    ".footer { margin-top: 20px; font-size: 12px; color: #666; }" +
                    ".success { color: #28a745; font-weight: bold; }" +
                    ".failed { color: #dc3545; font-weight: bold; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"header\">" +
                    "<h2>" + statusIcon + " Naukri Profile Update - " + statusText + "</h2>" +
                "</div>" +
                "<div class=\"content\">" +
                    "<h3>Execution Summary</h3>" +
                    "<div class=\"summary\">" +
                        "<strong>Date & Time:</strong> " + currentTime + "<br>" +
                        "<strong>Status:</strong> <span class=\"" + statusClass + "\">" + statusText + "</span><br><br>" +
                        "<strong>Details:</strong><br>" + executionSummary +
                    "</div>" +
                    "<h3>Next Steps</h3>" +
                    "<ul>" + nextSteps + "</ul>" +
                "</div>" +
                "<div class=\"footer\">" +
                    "<p>This is an automated notification from your Naukri Profile Update automation.</p>" +
                    "<p>If you have any issues, check the execution logs in GitHub Actions or Jenkins.</p>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
}
