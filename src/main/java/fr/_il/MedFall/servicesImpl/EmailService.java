package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.config.EmailConfig;
import fr._il.MedFall.enums.EmailTemplate;
import fr._il.MedFall.exceptions.EmailException;
import fr._il.MedFall.services.IEmailService;
import fr._il.MedFall.utils.EmailValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final EmailConfig emailConfig;

    /**
     * Send reset password email with retry mechanism
     */
    @Override
    @Async
    public void sendResetPwdMail(String to, String userName, String resetCode) {
        validateEmailParameters(to, userName, resetCode);

        Map<String, Object> properties = buildResetProperties(userName, resetCode,
                "Vous avez demandé la réinitialisation de votre mot de passe. " +
                        "Veuillez utiliser le code ci-dessous pour procéder à la récupération de votre compte :");

        sendEmailWithRetry(properties, to, "Récupération de Mot de Passe", EmailTemplate.RESET_PWD);
    }

    @Override
    public void sendResetEmailMail(String to, String user_name, String reset_code) {

    }


    /**
     * Validate email parameters
     */
    private void validateEmailParameters(String email, String userName, String resetCode) {
        EmailValidator.validateEmail(email);

        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }

        if (resetCode == null || resetCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Reset code cannot be null or empty");
        }
    }

    /**
     * Send email using template
     */
    private void sendEmail(Map<String, Object> properties, String to, String subject, EmailTemplate template)
            throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        // Set sender information
        helper.setFrom(emailConfig.getSenderEmail());
        helper.setTo(to);
        helper.setSubject(subject);

        // Process template
        Context context = new Context();
        context.setVariables(properties);
        String emailContent = templateEngine.process(template.getTemplatePath(), context);
        helper.setText(emailContent, true);

        // Send email
        mailSender.send(mimeMessage);

        if (emailConfig.isEnableLogging()) {
            log.debug("Email prepared and sent to: {} with template: {}", to, template.getTemplatePath());
        }
    }

    /**
     * Send email with retry mechanism
     */
    private void sendEmailWithRetry(Map<String, Object> properties, String to, String subject, EmailTemplate template) {
        String sanitizedEmail = EmailValidator.sanitizeEmail(to);

        for (int attempt = 1; attempt <= emailConfig.getMaxRetryAttempts(); attempt++) {
            try {
                sendEmail(properties, sanitizedEmail, subject, template);
                log.info("Email sent successfully to: {} (attempt {})", sanitizedEmail, attempt);
                return;
            } catch (Exception e) {
                log.warn("Email sending failed for: {} (attempt {}/{}): {}",
                        sanitizedEmail, attempt, emailConfig.getMaxRetryAttempts(), e.getMessage());

                if (attempt == emailConfig.getMaxRetryAttempts()) {
                    log.error("All email sending attempts failed for: {}", sanitizedEmail, e);
                    throw new EmailException("Failed to send email after " + emailConfig.getMaxRetryAttempts() + " attempts", e);
                }

                if (emailConfig.isEnableRetry()) {
                    try {
                        Thread.sleep(emailConfig.getRetryDelayMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new EmailException("Email sending interrupted", ie);
                    }
                }
            }
        }
    }

    /**
     * Build properties for reset emails
     */
    private Map<String, Object> buildResetProperties(String userName, String resetCode, String resetMessage) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("greeting", userName != null ? userName : "Utilisateur");
        properties.put("reset_code", resetCode);
        properties.put("reset_msg", resetMessage);
        properties.put("currentYear", LocalDateTime.now().getYear());
        return properties;
    }
}
