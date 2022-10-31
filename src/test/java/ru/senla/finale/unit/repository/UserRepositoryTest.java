package ru.senla.finale.unit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.senla.model.User;
import ru.senla.repository.UserRepository;

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
    void testBootstrapping() throws Exception {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Assertions.assertNull(us.getId());
        em.persist(us);
        Assertions.assertNotNull(us.getId());
    }

    @Test
    void testSave() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Assertions.assertNull(us.getId());
        underTest.save(us);
        Assertions.assertNotNull(us.getId());
    }

    @Test
    void testFindByUsername() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();

        underTest.save(us);

        Optional<User> check = underTest.findByUsername(us.getUsername());
        Assertions.assertNotNull(check.get());
        Assertions.assertEquals(check.get().getPassword(), us.getPassword());
    }
}
