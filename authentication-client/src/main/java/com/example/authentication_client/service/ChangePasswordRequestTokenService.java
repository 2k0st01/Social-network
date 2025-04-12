package com.example.authentication_client.service;

import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.model.token.ChangePasswordRequestToken;
import com.example.authentication_client.repository.ChangePasswordRequestTokenRepository;
import com.example.authentication_client.repository.UserAccountRepository;
import com.example.authentication_client.utils.EmailBuilder;
import com.example.authentication_client.utils.emailSender.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ChangePasswordRequestTokenService {
    private final ChangePasswordRequestTokenRepository changePasswordRequestTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailService emailService;

    @Transactional
    public void save(ChangePasswordRequestToken changePasswordRequestToken){
        changePasswordRequestTokenRepository.save(changePasswordRequestToken);
    }

    @Transactional
    public ChangePasswordRequestToken findChangePasswordRequestTokenByToken(String token){
        return changePasswordRequestTokenRepository.findChangePasswordRequestTokenByToken(token);
    }

    @Transactional
    public void deleteChangePasswordRequestTokenByToken(String token){
        changePasswordRequestTokenRepository.deleteChangePasswordRequestTokenByToken(token);
    }

    @Transactional
    public List<ChangePasswordRequestToken> findByConfirmedAtIsNull(){
        return changePasswordRequestTokenRepository.findByConfirmedAtIsNull();
    }

    @Transactional
    public void requestForRecoverPassword(String email){
        String link = "https://kostotestproject.it.com/aunt/changePassword?token=" + creatConfirmToken(email);
        emailService.send(email, EmailBuilder.requestForChanePassword("Dear user", link));
    }

    private String creatConfirmToken(String email) {
        Optional<UserAccount> userAccount = userAccountRepository.findUserAccountByEmail(email);
        String token = UUID.randomUUID().toString();
        Optional<ChangePasswordRequestToken> changePasswordRequestToken = userAccount.map(a -> new ChangePasswordRequestToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), a));
        changePasswordRequestToken.ifPresent(changePasswordRequestTokenRepository::save);
        return token;
    }

}
