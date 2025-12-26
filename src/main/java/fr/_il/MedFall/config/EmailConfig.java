package fr._il.MedFall.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "medfall.email")
@Validated
public class EmailConfig {
    @NotBlank(message = "Email sender name is required")
    private String senderName = "MedFall Support";

    @Email(message = "Invalid sender email format")
    @NotBlank(message = "Sender email is required")
    private String senderEmail;

    @NotBlank(message = "Support email is required")
    @Email(message = "Invalid support email format")
    private String supportEmail = "support@medfall.fr";

    @Min(value = 1, message = "Max retry attempts must be at least 1")
    private int maxRetryAttempts = 3;

    @Min(value = 1000, message = "Retry delay must be at least 1000ms")
    private long retryDelayMs = 5000;

    @NotNull(message = "Email timeout must be configured")
    private int timeoutMs = 10000;

    @NotNull(message = "Connection timeout must be configured")
    private int connectionTimeoutMs = 10000;

    @NotNull(message = "Write timeout must be configured")
    private int writeTimeoutMs = 10000;

    private boolean enableRetry = true;
    private boolean enableLogging = true;
}
