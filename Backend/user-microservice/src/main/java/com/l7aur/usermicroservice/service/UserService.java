package com.l7aur.usermicroservice.service;

import com.l7aur.usermicroservice.model.User;
import com.l7aur.usermicroservice.model.delete.DeleteReply;
import com.l7aur.usermicroservice.model.delete.DeleteRequest;
import com.l7aur.usermicroservice.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ResponseEntity<@NonNull List<User>> findAll() {
        try {
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull User> findByUsername(String username) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NO_CONTENT));
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull User> save(User user) {
        try {
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull User> update(User user) {
        try {
            Optional<User> oldUser = userRepository.findByUsername(user.getUsername());
            if(oldUser.isEmpty()) {
                return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
            }
            User newUser = oldUser.get();
            newUser.setUsername(user.getUsername());
            return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull DeleteReply> delete(DeleteRequest request) {
        try {
            userRepository.deleteAllById(request.getIds());
            return new ResponseEntity<>(new DeleteReply(request.getIds(), null), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new DeleteReply(request.getIds(), e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
