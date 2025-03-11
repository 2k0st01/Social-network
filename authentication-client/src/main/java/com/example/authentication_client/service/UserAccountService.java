package com.example.authentication_client.service;

import com.example.authentication_client.DTO.PasswordDTO;
import com.example.authentication_client.DTO.UserAccountDTO;
import com.example.authentication_client.DTO.UserInfo;
import com.example.authentication_client.kafka.KafkaProducerService;
import com.example.authentication_client.model.UserAccount;
import com.example.authentication_client.repository.UserAccountRepository;
import com.example.authentication_client.role.UserRoles;
import com.example.authentication_client.utils.AuntResponse;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaProducerService kafkaProducerService;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              PasswordEncoder passwordEncoder,
                              @Lazy JwtService jwtService,
                              @Lazy AuthenticationManager authenticationManager,
                              KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    @CacheEvict(cacheNames = {"loadUser","findByEmail","findByID","searchUsers","userExist"}, allEntries = true)
    @Transactional
    public boolean registration(UserAccountDTO request) {
        if (!request.getPassword().equals(request.getDoublePassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        boolean isRegister = userAccountRepository
                .existsUserAccountByEmailOrUsername(request.getEmail(), request.getUsername());

        if (!isRegister) {
            String encodePassword = passwordEncoder.encode(request.getPassword());
            UserAccount userAccount = new UserAccount();
            userAccount.setEmail(request.getEmail());
            userAccount.setUsername(request.getUsername());
            userAccount.setPassword(encodePassword);
            userAccount.setUserRoles(UserRoles.USER);

            userAccount = userAccountRepository.save(userAccount);

            kafkaProducerService.sendCreatNewUserAccountEvent(userAccount.getId());
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public AuntResponse authenticate(UserAccountDTO request) {
        UserAccount userAccount = userAccountRepository
                .findUserAccountByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found exception"));

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String jwtToken = jwtService.generateToken(userAccount);
        return new AuntResponse(jwtToken, userAccount.getId(), userAccount.getUsernameByUser(), userAccount.getEmail());
    }

    @Transactional
    public boolean recoverPassword(PasswordDTO passwordDTO, UserAccount userAccount) {
        if (passwordDTO.getNewPassword().equals(passwordDTO.getRepeatNewPassword())) {
            String newPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
            userAccount.setPassword(newPassword);
            userAccountRepository.save(userAccount);
            return true;
        }
        return false;
    }

    @Transactional
    @CacheEvict(value = "loadUser", key = "#email")
    public void changerPassword(PasswordDTO passwordDTO, String email, String token) {
        Optional<UserAccount> userAccount = userAccountRepository.findUserAccountByEmail(email);

        if (userAccount.isEmpty()) throw new IllegalStateException("Token not valid.");

        if (!jwtService.isTokenValid(token, userAccount.get()))
            throw new UsernameNotFoundException("User not found exception.");

        String lastPassword = (userAccount.get()).getPassword();

        if (!passwordEncoder.matches(passwordDTO.getLastPassword(), lastPassword)) {
            throw new UsernameNotFoundException("Your last password doesn't match.");
        }

        String newEncodePassword = this.passwordEncoder.encode(passwordDTO.getNewPassword());
        (userAccount.get()).setPassword(newEncodePassword);
        userAccountRepository.save((userAccount.get()));
    }

    @Transactional(readOnly = true)
    public List<String> findUserNamesById(String firstId, String secondId) {
        Optional<UserAccount> u1 = userAccountRepository.findUserAccountById(Long.valueOf(firstId));
        Optional<UserAccount> u2 = userAccountRepository.findUserAccountById(Long.valueOf(secondId));
        if (u1.isPresent() && u2.isPresent()) {
            return List.of((u1.get()).getUsernameByUser(), (u2.get()).getUsernameByUser());
        }
        return Collections.emptyList();
    }

    @Cacheable(value = "searchUsers", key = "#username + ':' + #token")
    @Transactional(readOnly = true)
    public List<UserInfo> findUserAccountByUserName(String username, String token, String email) {
        Optional<UserAccount> userAccount;
        if (username.length() >= 3 && (userAccount = userAccountRepository.findUserAccountByEmail(email)).isPresent() && jwtService.isTokenValid(token, userAccount.get())) {
            return userAccountRepository.findTop10UserAccountByUsernameStartingWith(username).stream().map(a -> {
                UserInfo ui = new UserInfo();
                ui.setId(a.getId());
                ui.setUsername(a.getUsernameByUser());
                return ui;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Cacheable(value = "loadUser", key = "#email")
    @Transactional(readOnly = true)
    public UserAccount loadUserByUsername(String email) throws UsernameNotFoundException {
        return userAccountRepository.findUserAccountByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found exception"));
    }

    @Cacheable(value = "findByEmail", key = "#email")
    @Transactional(readOnly = true)
    public Optional<UserAccount> findUserAccountByEmail(String email) {
        return userAccountRepository.findUserAccountByEmail(email);
    }

    @Cacheable(value = "userExist", key = "#id")
    @Transactional(readOnly = true)
    public boolean existsUserAccountById(Long id) {
        return userAccountRepository.existsUserAccountById(id);
    }

    @Cacheable(value = "findByID", key = "#id")
    @Transactional(readOnly = true)
    public Optional<UserAccount> findUserAccountById(Long id) {
        return userAccountRepository.findUserAccountById(id);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> findUserAccountsByIds(Set<Long> id) {
        List <UserAccount> userAccount = userAccountRepository.findByIdIn(id);
        Map<Long, String> map = new HashMap<>();

        userAccount.forEach(a -> map.put(a.getId(),a.getUsernameByUser()));

        return map;
    }
}

