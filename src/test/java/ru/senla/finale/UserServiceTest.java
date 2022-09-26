package ru.senla.finale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.senla.model.User;
import ru.senla.repository.UserRepository;
import ru.senla.service.UserService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void loadUserByUsername() {
        User user = new User();
        user.setUsername("user");

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Assertions.assertEquals(user.getUsername(), underTest.loadUserByUsername(user.getUsername()).getUsername());
    }

    @Test
    public void create() {
        //Arrange
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("pass"));
        //Act
        underTest.create(user);
        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);
        //Assert
        Mockito.verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        Assertions.assertEquals(captured, user);
    }

    @Test
    public void update() {
        //Arrange
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("pass"));
        //Act
        underTest.update(user);
        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);
        //Assert
        Mockito.verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        Assertions.assertEquals(captured, user);
    }
}
