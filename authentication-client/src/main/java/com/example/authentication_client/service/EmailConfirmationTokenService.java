package com.example.authentication_client.service;

import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.model.token.EmailConfirmationToken;
import com.example.authentication_client.repository.EmailConfirmationTokenRepository;
import com.example.authentication_client.repository.UserAccountRepository;
import com.example.authentication_client.utils.EmailBuilder;
import com.example.authentication_client.utils.emailSender.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailConfirmationTokenService {
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public Optional<EmailConfirmationToken> getToken(String token) {
        return emailConfirmationTokenRepository.findByToken(token);
    }

    @Transactional(readOnly = true)
    public EmailConfirmationToken findConfirmationTokenByUserAccount(UserAccount userAccount) {
        return emailConfirmationTokenRepository.findConfirmationTokenByUserAccount(userAccount);
    }

    @Transactional(readOnly = true)
    public List<EmailConfirmationToken> findByConfirmedAtIsNull() {
        return emailConfirmationTokenRepository.findByConfirmedAtIsNull();
    }

    @Transactional
    public void deleteByToken(String token) {
        emailConfirmationTokenRepository.deleteByToken(token);
    }

    @Transactional
    public String requestForEmailConfirmation(String email) {
        String link = "http://localhost:8763/aunt/confirm?token=" + createConfirmToken(email);
        emailService.send(email, EmailBuilder.confirmYourEmail("Dear user", link));
        return "Send";
    }

    private String createConfirmToken(String email) {
        return userAccountRepository.findUserAccountByEmail(email)
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), user
                    );
                    emailConfirmationTokenRepository.save(emailConfirmationToken);
                    return token;
                })
                .orElseThrow(() -> new IllegalStateException("User account not found"));
    }

    @Transactional
    public String confirmToken(String token) {
        EmailConfirmationToken emailConfirmationToken = getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (emailConfirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        if (emailConfirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        emailConfirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
        userAccountRepository.enableAppUser(emailConfirmationToken.getUserAccount().getEmail());
        return "confirmed";
    }
}
