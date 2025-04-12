package com.example.rating_service.kafka;

import com.example.rating_service.enums.RatingEnums;
import com.example.rating_service.model.rating.RatingService;
import com.example.rating_service.model.userRating.UserRatingService;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerService {
    private final RatingService ratingService;
    private final UserRatingService userRatingService;

    @KafkaListener(topics={"new.user.account"}, groupId="rating-service")
    public void consumePhotoUploaded(String event) {
        Long id = Long.valueOf(event);
        if (ratingService.existsRatingsByRatingOwnID(id)) {
            ratingService.create(id);
        }
    }

    @KafkaListener(topics = "rating.event", groupId = "rating-service")
    public void incDecRating(ConsumerRecord<String, String> record) {
        String event = record.value();
        String[] message = event.replace("\"", "").split(";");
        userRatingService.incrementDecrementRating(
                Long.valueOf(message[0]),
                Long.valueOf(message[1]),
                RatingEnums.valueOf(message[2])
        );
    }
}
