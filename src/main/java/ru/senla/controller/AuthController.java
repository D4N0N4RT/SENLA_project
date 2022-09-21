package ru.senla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.senla.dto.AuthDTO;
import ru.senla.dto.IUserDTO;
import ru.senla.dto.UpdateUserDTO;
import ru.senla.dto.UserDTO;
import ru.senla.exception.DuplicateUsernameException;
import ru.senla.exception.PasswordCheckException;
import ru.senla.mapper.UserMapper;
import ru.senla.model.User;
import ru.senla.security.JwtTokenProvider;
import ru.senla.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager manager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
                          UserMapper userMapper) {
        this.manager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO userDTO) throws DuplicateUsernameException, PasswordCheckException {
        try {
            userService.loadUserByUsername(userDTO.getUsername());
            throw new DuplicateUsernameException("Данная почта уже используется для другого аккаунта");
        } catch(UsernameNotFoundException ex) {
            checkDTO(userDTO);
            User user = userDTO.toUser();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.create(user);
            return new ResponseEntity<>("Вы успешно зарегистрировались в системе", HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthDTO request) throws UsernameNotFoundException {
        String username = request.getUsername();
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        User user = (User) userService.loadUserByUsername(request.getUsername());
        String token = jwtTokenProvider.createToken(username, user.getRole().name());
        Map<Object, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestBody @Valid UpdateUserDTO dto, HttpServletRequest request)
            throws PasswordCheckException {
        String pass = checkDTO(dto);
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        userMapper.updateUserFromDto(dto, user);
        if (pass != null) {
            user.setPassword(pass);
            userService.update(user);
        }
        return new ResponseEntity<>("Данные вашего профиля обновлены", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.logout(request, response, null);
    }

    public String checkDTO(IUserDTO dto) throws PasswordCheckException {
        if (dto.getPassword() != null) {
            String CHECK = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+)[a-zA-Z0-9_-]{8,}$";
            Pattern pattern = Pattern.compile(CHECK);
            Matcher matcher = pattern.matcher(dto.getPassword());
            if (!matcher.matches()) {
                throw new PasswordCheckException("Пароль должен содержать как миниму одну строчную букву, " +
                        "одну заглавную букву и одну цифру, а также быть не менне 8 символов в длину.");
            }
            return passwordEncoder.encode(dto.getPassword());
        }
        return null;
    }
}
