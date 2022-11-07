package ru.senla.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.exception.EmptyResponseException;
import ru.senla.model.Message;
import ru.senla.model.User;
import ru.senla.repository.MessageRepository;

import java.util.List;

@Slf4j
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(User user1, User user2) throws EmptyResponseException {
        log.info("Get conversation between user {} and user {}", user1.getUsername(), user2.getUsername());
        List<Message> messages = messageRepository.findConversation(user1, user2, Sort.by("time"));
        if (messages.isEmpty())
            throw new EmptyResponseException("Диалог с данным пользователем пуст");
        return messages;
    }

    @Transactional
    public void create(Message message) {
        log.info("Send message to user {} by user {}", message.getReceiver().getUsername(),
                message.getSender().getUsername());
        messageRepository.save(message);
    }
}
