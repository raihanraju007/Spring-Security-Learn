package service;

import entity.AuthenticationResponse;
import entity.Role;
import entity.Token;
import entity.User;
import jakarta.mail.MessagingException;
import jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.TokenRepository;
import repository.UserRepository;

import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenRepository tokenRepository, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    private void saveUseToken(String jwt, User user) {
//        System.out.println("Saving Token: " + jwt + " for User ID: " + user.getId());
        Token token = new Token();
        token.setToken(jwt);
        token.setLogout(false);
        token.setUser(user);
        tokenRepository.save(token);

//        System.out.println("Token saved successfully!");
    }

    private void removeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokenByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(t -> {
            t.setLogout(true);
        });
        tokenRepository.saveAll(validTokens);
    }


    public AuthenticationResponse register(User user) {

        // We check that already any user exist with this email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new AuthenticationResponse(null, "User already exist");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.valueOf("USER"));
        user.setLock(true);
        user.setActive(false);

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
//        System.out.println("---------------------");
//        System.out.println("Generated Token: " + jwt);
//        System.out.println("---------------------");

        saveUseToken(jwt, user);

        sendActivationEmail(user);

        return new AuthenticationResponse(jwt, "User registration successful");
    }

    public AuthenticationResponse authentication(User request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        // Generate token for current user
        String jwt = jwtService.generateToken(user);
        // Remove all existing token for this user
        removeAllTokenByUser(user);
        saveUseToken(jwt, user);
        return new AuthenticationResponse(jwt, "User Login Successful");
    }

    public String activeUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with this ID" + id));
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);
            return "User Activate Successfully";
        } else {
            return "Invalid Activation Token!";
        }
    }


    private void sendActivationEmail(User user) {
        String activationLink = "http://localhost:8080/active/" + user.getId();
        String mailText = " <h2> Dear </h2>" + user.getName() + ","
                + "<p> Please click on the following link to conform your registration</p>"
                + "<a href=\"" + activationLink + "\">Active Account</a>";
        String subject = "Confirmation Registration";

        try {
            emailService.sendSimpleEmail(user.getEmail(), subject, mailText);
        } catch (MessagingException messagingException) {
            throw new RuntimeException();
        }
    }



}
