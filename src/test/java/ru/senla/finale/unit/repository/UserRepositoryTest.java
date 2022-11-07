package ru.senla.finale.unit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.senla.model.User;
import ru.senla.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository underTest;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }


    @Test
    void testBootstrapping() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Assertions.assertNull(us.getId());
        em.persist(us);
        Assertions.assertNotNull(us.getId());
    }

    @Test
    void shouldSave() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Assertions.assertNull(us.getId());
        underTest.save(us);
        Assertions.assertNotNull(us.getId());
    }

    @Test
    void shouldUpdate() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Assertions.assertNull(us.getId());
        underTest.save(us);
        Assertions.assertNotNull(us.getId());

        us.setPassword("pass2");
        underTest.save(us);

        User user = underTest.findById(1L).get();

        Assertions.assertEquals(user.getPassword(), "pass2");
        Assertions.assertEquals(user.getUsername(), "mail@mail.ru");
    }

    @Test
    void shouldDelete() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Assertions.assertNull(us.getId());
        underTest.save(us);
        Assertions.assertNotNull(us.getId());

        underTest.delete(us);

        List<User> check = underTest.findAll();

        Assertions.assertEquals(check.size(), 0);
    }

    @Test
    void shouldFindByUsername() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();

        underTest.save(us);

        Optional<User> check = underTest.findByUsername(us.getUsername());
        Assertions.assertNotNull(check.get());
        Assertions.assertEquals(check.get().getPassword(), us.getPassword());
    }
}
