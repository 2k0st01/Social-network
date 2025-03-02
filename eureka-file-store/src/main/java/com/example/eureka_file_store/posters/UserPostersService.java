package com.example.eureka_file_store.posters;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserPostersService {
    private final UserPostersRepository userPostersRepository;

    @Transactional
    public void saveUserPoster(Long userId, String posterURL) {
        Optional<UserPorters> userPosters = userPostersRepository.findById(userId);
        if (userPosters.isPresent()) {
            (userPosters.get()).addPoster(posterURL);
            userPostersRepository.save(userPosters.get());
        } else {
            UserPorters userPorters = new UserPorters(userId, posterURL);
            userPostersRepository.save(userPorters);
        }
    }
}
