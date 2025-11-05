package com.l7aur.devicemicroservice.service;

import com.l7aur.devicemicroservice.model.User;
import com.l7aur.devicemicroservice.model.delete.UserDeleteReply;
import com.l7aur.devicemicroservice.model.delete.UserDeleteRequest;
import com.l7aur.devicemicroservice.repository.DeviceRepository;
import com.l7aur.devicemicroservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private DeviceRepository deviceRepository;

    public ResponseEntity<@NonNull List<User>> findAll() {
        try {
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception ex) {
            return  new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull User> findByUsername(String username) {
        try {
            Optional<User> user = userRepository.findUserByUsername(username);
            return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        catch (Exception ex) {
            return  new ResponseEntity<>(new User(0, username), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull User> save(@NonNull User user) {
        try {
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
        }
        catch (Exception ex) {
            return  new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<@NonNull UserDeleteReply> delete(@NonNull UserDeleteRequest request) {
        try {
            request.getUsernames().forEach(username -> {
                deviceRepository.deleteByUser_Username(username);
                userRepository.deleteAllByUsername(username);
            });
            return new ResponseEntity<>(new UserDeleteReply(request.getUsernames()), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new UserDeleteReply(Collections.emptyList()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
