package com.twilio.sms2fa.domain.service;

import com.twilio.sms2fa.domain.exception.WrongUserPasswordException;
import com.twilio.sms2fa.domain.model.User;
import com.twilio.sms2fa.domain.repository.UserRepository;

import java.util.Optional;

public class LogIn {

    private UserRepository userRepository;
    private MessageSender messageSender;

    public LogIn(UserRepository userRepository, MessageSender messageSender) {
        this.userRepository = userRepository;
        this.messageSender = messageSender;
    }

    public void authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        user.ifPresent(user1 -> {
            if (user1.authenticate(password)) {
                user1.generateNewVerificationCode();
                userRepository.save(user1);
                messageSender.sendCode(user1);
            } else {
                throw new WrongUserPasswordException();
            }
        });
        user.orElseThrow(WrongUserPasswordException::new);
    }
}
