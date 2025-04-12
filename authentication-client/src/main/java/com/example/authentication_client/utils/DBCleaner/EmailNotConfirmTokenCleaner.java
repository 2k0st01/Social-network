package com.example.authentication_client.utils.DBCleaner;

import com.example.authentication_client.model.token.EmailConfirmationToken;
import com.example.authentication_client.service.EmailConfirmationTokenService;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Component
public class EmailNotConfirmTokenCleaner {
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private static final long TIME = 60L; // 60 хвилин

    @Scheduled(fixedRate = 1800000L) // 30 хвилин
    public void checkValidTokens() {
        List<EmailConfirmationToken> tokenList = emailConfirmationTokenService.findByConfirmedAtIsNull();
        if (tokenList != null) {
            tokenList.stream()
                    .filter(token -> Duration.between(token.getExpiresAt(), LocalDateTime.now()).toMinutes() >= TIME)
                    .forEach(token -> emailConfirmationTokenService.deleteByToken(token.getToken()));
        }
    }
}
