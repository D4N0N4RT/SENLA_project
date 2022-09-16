package ru.senla.finale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.exception.EmptyResponseException;
import ru.senla.model.Message;
import ru.senla.model.User;
import ru.senla.repository.MessageRepository;
import ru.senla.service.MessageService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;
    private MessageService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new MessageService(messageRepository);
    }

    @Test
    public void create() {
        Message message = new Message();
        message.setText("Test");

        User user1 = new User();
        user1.setUsername("user");
        User user2 = new User();
        user2.setUsername("user2");

        message.setReceiver(user2);
        message.setSender(user1);

        underTest.create(message);

        ArgumentCaptor<Message> captor =
                ArgumentCaptor.forClass(Message.class);

        Mockito.verify(messageRepository).save(captor.capture());

        Message captured = captor.getValue();
        Assertions.assertEquals(captured, message);
    }

    @Test
    public void getConversation() throws EmptyResponseException {
        Message message = new Message();
        message.setText("Test");
        message.setTime(LocalDateTime.now());

        User user1 = new User();
        user1.setUsername("user");
        User user2 = new User();
        user2.setUsername("user2");

        Mockito.when(messageRepository.findAllBySenderAndReceiver(user1,user2)).thenReturn(new ArrayList<>(List.of(message)));
        Mockito.when(messageRepository.findAllBySenderAndReceiver(user2,user1)).thenReturn(List.of(message));

        List<Message> check = underTest.getConversation(user1,user2);

        Mockito.verify(messageRepository).findAllBySenderAndReceiver(user1,user2);
        Mockito.verify(messageRepository).findAllBySenderAndReceiver(user2,user1);
        Assertions.assertNotNull(check);
        Assertions.assertEquals(2, check.size());
    }
}
