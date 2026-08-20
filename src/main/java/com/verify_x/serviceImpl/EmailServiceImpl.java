package com.verify_x.serviceImpl;

import com.verify_x.services.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
//    @Value("${spring.mail.username}")
//    private String fromEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @PostConstruct
    public void checkMail() {
        System.out.println("================================");
        System.out.println("MAIL USER : " + fromEmail);
        System.out.println("================================");
    }

    @Override
    public void sendApplicationApprovedEmail(
            String to,
            String candidateName,
            String remarks) {

        String subject = "VerifyX | Application Approved";

        String body = """
                <html>
                <body>

                <h2>Congratulations %s!</h2>

                <p>Your application has been <b>APPROVED</b>.</p>

                <p><b>HR Remarks:</b></p>

                <p>%s</p>

                <br>

                <p>Regards,</p>

                <b>VerifyX HR Team</b>

                </body>
                </html>
                """
                .formatted(candidateName, remarks);

        sendHtmlMail(to, subject, body);
    }

    @Override
    public void sendApplicationRejectedEmail(
            String to,
            String candidateName,
            String remarks) {

        String subject = "VerifyX | Application Rejected";

        String body = """
                <html>
                <body>

                <h2>Hello %s,</h2>

                <p>Unfortunately your application has been <b>REJECTED</b>.</p>

                <p><b>Reason:</b></p>

                <p>%s</p>

                <br>

                <p>Regards,</p>

                <b>VerifyX HR Team</b>

                </body>
                </html>
                """
                .formatted(candidateName, remarks);

        sendHtmlMail(to, subject, body);
    }

    @Override
    public void sendReUploadRequestEmail(
            String to,
            String candidateName,
            String remarks) {

        String subject = "VerifyX | Document Re-upload Required";

        String body = """
                <html>
                <body>

                <h2>Hello %s,</h2>

                <p>Your application requires document re-upload.</p>

                <p><b>HR Remarks:</b></p>

                <p>%s</p>

                <br>

                <p>Please login to VerifyX and upload the requested documents.</p>

                <br>

                <b>VerifyX HR Team</b>

                </body>
                </html>
                """
                .formatted(candidateName, remarks);

        sendHtmlMail(to, subject, body);
    }

    /**
     * Common Email Sender
     */
    private void sendHtmlMail(
            String to,
            String subject,
            String body) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

        } catch (MessagingException | MailException ex) {

            throw new RuntimeException(
                    "Unable to send email.",
                    ex
            );
        }
    }

    @Override
    public void sendOtp(String email, String otp) {

        String subject = "VerifyX | OTP Verification";

        String body = """
                <html>
                <body>

                <h2>Hello,</h2>

                <p>Your OTP for VerifyX is:</p>

                <h3>%s</h3>

                <br>

                <p>Please use this OTP to complete your verification process.</p>

                <br>

                <b>VerifyX Team</b>

                </body>
                </html>
                """
                .formatted(otp);

        sendHtmlMail(email, subject, body);
    }

}