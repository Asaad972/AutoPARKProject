package utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Utility class for sending emails using JavaMail API.
 */
public class EmailUtil {

    /**
     * Sends an email to the specified recipient.
     *
     * @param toEmail   The recipient's email address.
     * @param subject   The subject of the email.
     * @param body      The content of the email.
     * @throws MessagingException If sending the email fails.
     */
    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        // Set up mail server properties
    	Properties props = new Properties();
    	props.put("mail.smtp.host", "smtp.gmail.com");  // Gmail SMTP server
    	props.put("mail.smtp.port", "587");             // TLS port (use 465 only for SSL)
    	props.put("mail.smtp.auth", "true");            // Enable authentication
    	props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS (TLS security)


        // Authenticate with your SMTP server
        final String fromEmail = "bpark.g11@gmail.com";
        final String password = "sockgizfmbhjszvn";       

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Compose the email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        // Send the email
        Transport.send(message);
    }
}