package com.example.eurekaclient.messenger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessagesService {
    private final MessagesRepository messagesRepository;

    @Transactional
    public void save(Messages messages) {
        this.messagesRepository.save(messages);
    }

    public MessagesService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }
}
