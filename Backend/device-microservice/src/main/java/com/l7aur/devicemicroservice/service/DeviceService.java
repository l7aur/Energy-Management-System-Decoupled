package com.l7aur.devicemicroservice.service;

import com.l7aur.devicemicroservice.model.User;
import com.l7aur.devicemicroservice.model.delete.DeleteReply;
import com.l7aur.devicemicroservice.model.delete.DeleteRequest;
import com.l7aur.devicemicroservice.model.Device;
import com.l7aur.devicemicroservice.repository.DeviceRepository;
import com.l7aur.devicemicroservice.repository.UserRepository;
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
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public ResponseEntity<@NonNull List<Device>> findAll() {
        try {
            return new ResponseEntity<>(deviceRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull List<Device>> findByUsername(String username) {
        try {
            return new ResponseEntity<>(deviceRepository.findByUser_Username(username), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull Device> save(Device device) {
        try {
            return new ResponseEntity<>(deviceRepository.save(device), HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(device, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull Device> update(Device device) {
        try {
            Optional<Device> oldDevice = deviceRepository.findById(device.getId());
            Optional<User> newUser = userRepository.findById(device.getUser().getId());

            if (oldDevice.isEmpty() || newUser.isEmpty())
                return new ResponseEntity<>(device, HttpStatus.NOT_FOUND);

            Device newDevice = oldDevice.get();
            newDevice.setUser(newUser.get());
            newDevice.setMaximumConsumptionValue(device.getMaximumConsumptionValue());
            newDevice.setName(device.getName());
            return new ResponseEntity<>(deviceRepository.save(newDevice), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(device, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<@NonNull DeleteReply> delete(DeleteRequest request) {
        try {
            deviceRepository.deleteAllById(request.getIds());
            return new ResponseEntity<>(new DeleteReply(request.getIds()), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new DeleteReply(Collections.emptyList()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
