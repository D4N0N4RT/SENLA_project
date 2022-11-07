package ru.senla.finale.integration.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.senla.model.Message;
import ru.senla.model.User;
import ru.senla.repository.MessageRepository;
import ru.senla.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class MessageRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private MessageRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testBootstrapping() {
        User us1 = User.builder().username("mail@mail.ru").password("pass").build();
        User us2 = User.builder().username("mail2@mail.ru").password("pass").build();
        Message mes = Message.builder().sender(us1).receiver(us2).content("test")
                        .time(LocalDateTime.now()).build();
        Assertions.assertNull(mes.getId());
        em.persist(mes);
        Assertions.assertNotNull(mes.getId());
    }

    @Test
    void shouldSave() {
        User us1 = User.builder().username("mail@mail.ru").password("pass").build();
        User us2 = User.builder().username("mail2@mail.ru").password("pass").build();
        Message mes = Message.builder().sender(us1).receiver(us2).content("test")
                .time(LocalDateTime.now()).build();
        Assertions.assertNull(mes.getId());
        underTest.save(mes);
        Assertions.assertNotNull(mes.getId());
    }

    @Test
    void shouldReturnConversation() {
        User us1 = User.builder().username("mail@mail.ru").password("pass").build();
        User us2 = User.builder().username("mail2@mail.ru").password("pass").build();
        userRepository.save(us1);
        userRepository.save(us2);
        Message mes = Message.builder().sender(us1).receiver(us2).content("test")
                .time(LocalDateTime.now()).build();
        Assertions.assertNull(mes.getId());
        underTest.save(mes);
        Assertions.assertNotNull(mes.getId());

        List<Message> check = underTest.findConversation(us1, us2, Sort.by("time"));

        Assertions.assertNotNull(check);
        Assertions.assertEquals(check.size(), 1);
    }
}
