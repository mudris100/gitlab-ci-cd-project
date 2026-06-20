package dev.udris.service;

import dev.udris.dto.UserDto;
import dev.udris.dto.UserRegistrationDto;
import dev.udris.entity.Role;
import dev.udris.entity.User;
import dev.udris.exception.UsernameAlreadyTakenException;
import dev.udris.mapper.UserMapper;
import dev.udris.repository.UserRepository;
import dev.udris.service.kafka.UserEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ObjectProvider<UserEventPublisher> userEventPublisher;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper userMapper, ObjectProvider<UserEventPublisher> userEventPublisher) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userEventPublisher = userEventPublisher;
    }

    public User findByUsernameOrThrow(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new dev.udris.exception.NotFoundException("User", "username", username));
    }

    public List<UserDto> findAllUsersAsDto() {
        List<User> users = repository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public User findUserByIdOrThrow(Integer id) {
        return repository.findById(id).orElseThrow(() -> new dev.udris.exception.NotFoundException("User", "id", id));
    }

    public UserDto findUserDtoByIdOrThrow(Integer id) {
        User user = findUserByIdOrThrow(id);
        return userMapper.toDto(user);
    }

    public void deleteUserById(Integer id) {
        if (!repository.existsById(id)) {
            throw new dev.udris.exception.NotFoundException("User", "id", id);
        }
        repository.deleteById(id);
    }

    public User createUser(UserRegistrationDto dto) {
        if (repository.findByProviderAndUsername("local", dto.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException("Username already taken");
        }
        User user = userMapper.toEntity(dto);
        user.setRole(Role.USER);
        user.setProvider("local");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = repository.save(user);
        User finalUser = user;
        userEventPublisher.ifAvailable(userEvent -> userEvent.sendEvent(userMapper.toDto(finalUser)));
        return user;
    }

    public List<User> createAllUsers(List<User> users) {
        logger.info("Attempting to create {} users.", users.size());
        for (User user : users) {
            user.setProvider("local");
            if (user.getRole() == null) {
                user.setRole(Role.USER);
                logger.debug("Role not set for user '{}', assigning default ROLE.USER.", user.getUsername());

            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        List<User> savedUsers = repository.saveAll(users);
        logger.info("Successfully created {} users.", savedUsers.size());
        return savedUsers;
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = findUserByIdOrThrow(id);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }
        existingUser.setPosts(null);
        return repository.save(existingUser);
    }
}
