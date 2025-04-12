package com.example.post.builder;

import com.example.post.config.DataProcessor;
import com.example.post.modelDTO.PostDTO;
import com.example.post.modelDTO.SortDTO;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Component
public class Builder {
    private final DataProcessor dataProcessor;

    public Builder(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }


    public List<PostDTO> newsBuilder(Long userID, int page) {
        List<SortDTO> sortIds = dataProcessor.sortUserIds(userID, page);
        Map<Long, Integer> userRatings = sortIds.stream()
                .collect(Collectors.toMap(SortDTO::getUserID, SortDTO::getRating));

        System.out.println(sortIds);

        List<PostDTO> posts = dataProcessor.getPostsForFeedNews(sortIds, userID, page);
        LocalDateTime now = LocalDateTime.now();

        System.out.println(posts);

        return posts.stream()
                .filter(post -> post.getCountWatch() == null || post.getCountWatch() <= 6)
                .sorted(Comparator.comparingDouble(post -> calculatePostPriority((PostDTO) post, userRatings, now)).reversed())
                .limit(25)
                .collect(Collectors.toList());
    }

    private double calculatePostPriority(PostDTO post, Map<Long, Integer> userRatings, LocalDateTime now) {
        double w1 = 0.4;
        double w2 = 0.3;
        double w3 = 0.2;
        double w4 = 0.1;

        int countLike = post.getCountLike() != null ? post.getCountLike() : 0;
        int countWatch = post.getCountWatch() != null ? post.getCountWatch() : 0;
        int rating = userRatings.getOrDefault(post.getOwnID(), 0);
        long timeDifference = ChronoUnit.HOURS.between(post.getTime(), now);
        double timeDecay = 1.0 / (1.0 + (double) timeDifference);

        return w1 * countLike - w2 * countWatch + w3 * rating - w4 * timeDecay;
    }
}
