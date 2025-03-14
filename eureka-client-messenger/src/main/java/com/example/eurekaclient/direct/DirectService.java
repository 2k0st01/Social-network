package com.example.eurekaclient.direct;

import com.example.eurekaclient.config.DataProcessor;
import com.example.eurekaclient.dto.DirectDTO;
import com.example.eurekaclient.dto.MessagesDTO;
import com.example.eurekaclient.kafka.KafkaProducerService;
import com.example.eurekaclient.messenger.Messages;
import com.example.eurekaclient.messenger.MessagesRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DirectService {
    private final DirectRepository directRepository;
    private final MessagesRepository messagesRepository;
    private final DataProcessor dataProcessor;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public Optional<Direct> createDirect(String firstUserId, String secondUserId) {
        Direct direct = new Direct();
        List<String> names = dataProcessor.getUserNamesById(firstUserId, secondUserId);

        if (names.size() < 2) {
            throw new IllegalStateException("Expected at least 2 elements in the list, but got: " + names.size());
        }
        direct.setFirstUserId(firstUserId);
        direct.setSecondUserId(secondUserId);
        direct.setFirstUserName(names.get(0));
        direct.setSecondUserName(names.get(1));
        directRepository.save(direct);
        return Optional.of(direct);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "direct", key = "#userId"),
            @CacheEvict(value = "direct", key = "#toUser"),
            @CacheEvict(value = "massages", key = "#toUser + ':' + #userId"),
            @CacheEvict(value = "massages", key = "#userId + ':' + #toUser")
    })
    public boolean send(String userName, String userId, String toUser, String message) {
        boolean userExist = dataProcessor.checkUserExists(toUser);
        if (!userExist) {
            return false;
        }
        Optional<Direct> direct = directRepository.findDirectByUserIds(userId,toUser);
        if (direct.isEmpty()) {
            direct = createDirect(userId, toUser);
        }

        if (direct.isPresent()) {
            direct.get().setLastTimeUpdate(LocalDateTime.now());
            direct.get().setLastMassage(message);
            Messages messages = new Messages();
            messages.setDirect(direct.get());
            messages.setMessenger(message);
            messages.setTime(LocalDateTime.now());
            messages.setUserName(userName);
            direct.get().addMessages(messages);
            messagesRepository.save(messages);
            directRepository.save(direct.get());
            kafkaProducerService.incDecRating(userId, toUser, "MESSAGE");
            return true;
        }
        return false;
    }

    @Transactional
    @Cacheable(value = "direct", key = "#userId")
    public List<DirectDTO> getDirects(String userId, String username, Integer page) {
        PageRequest pageable = PageRequest.of(page, 25, Sort.by("lastTimeUpdate").descending());
        Page<DirectDTO> directs = directRepository.getAllDirectsByUserNameAsDTO(userId, username, pageable);
        if (directs.isEmpty()) {
            throw new IllegalStateException("Direct wasn't found.");
        }
        return directs.getContent();
    }

    @Transactional(readOnly=true)
    @Cacheable(cacheNames = "massages", key = "#firstID + ':' + #secondID")
    public List<MessagesDTO> getMessages(String firstID, String username, String secondID) {
        PageRequest pageable = PageRequest.of(0, 25, Sort.by("time").descending());
        return getMessages(firstID,username,secondID,pageable);
    }

    @Transactional(readOnly=true)
    public List<MessagesDTO> getMessages(String firstID, String username, String secondID, Integer page) {
        PageRequest pageable = PageRequest.of(page, 25, Sort.by("time").descending());
        return getMessages(firstID,username,secondID,pageable);
    }

    private List<MessagesDTO> getMessages(String firstID, String username, String secondID, PageRequest pageable){
        Optional<Direct> direct = directRepository.findDirectByUserIds(firstID, secondID);
        if (direct.isEmpty()) {
            return Collections.emptyList();
        }
        if (direct.get().getFirstUserId().equals(secondID) || direct.get().getSecondUserId().equals(secondID)) {
            return messagesRepository.getMessageDTObyDirectId(direct.get().getId(), username, pageable).getContent();
        }
        return Collections.emptyList();
    }



}
