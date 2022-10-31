package ru.senla.finale.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
        //Arrange
        Message message = new Message();
        message.setContent("Test");
        User user1 = User.builder().username("user").build();
        User user2 = User.builder().username("user2").build();
        message.setReceiver(user2);
        message.setSender(user1);
        //Act
        underTest.create(message);
        ArgumentCaptor<Message> captor =
                ArgumentCaptor.forClass(Message.class);
        //Assert
        Mockito.verify(messageRepository).save(captor.capture());
        Message captured = captor.getValue();
        Assertions.assertEquals(captured, message);
    }

    @Test
    public void getConversation() throws EmptyResponseException {
        //Arrange
        Message message = Message.builder().content("Test")
                .time(LocalDateTime.now()).build();
        User user1 = new User();
        user1.setUsername("user");
        User user2 = new User();
        user2.setUsername("user2");
        Mockito.when(messageRepository.findConversation(user1,user2, Sort.by("time"))).thenReturn(new ArrayList<>(List.of(message)));
/*        Mockito.when(messageRepository.findAllBySenderAndReceiver(user2,user1)).thenReturn(List.of(message));*/
        //Act
        List<Message> check = underTest.getConversation(user1,user2);
        //Assert
        Mockito.verify(messageRepository).findConversation(user1,user2, Sort.by("time"));
        //Mockito.verify(messageRepository).findAllBySenderAndReceiver(user2,user1);
        Assertions.assertNotNull(check);
        Assertions.assertEquals(2, check.size());
    }
}
