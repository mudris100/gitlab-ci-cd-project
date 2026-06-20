package dev.udris.service;

import dev.udris.dto.UserRegistrationDto;
import dev.udris.entity.Role;
import dev.udris.entity.User;
import dev.udris.exception.NotFoundException;
import dev.udris.exception.UsernameAlreadyTakenException;
import dev.udris.mapper.UserMapper;
import dev.udris.repository.UserRepository;
import dev.udris.service.UserService;
import dev.udris.service.kafka.UserEventPublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ObjectProvider<UserEventPublisher> userEventPublisher;
    @InjectMocks
    private UserService userService;

    private UserRegistrationDto dto;
    private User mappedUser;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDto();
        dto.setUsername("martins");
        dto.setPassword("plainPass");

        mappedUser = new User();
        mappedUser.setUsername("martins");
        mappedUser.setPassword("plainPass");
    }

    @Test
    void shouldCreateUser() {
        User savedUser = new User();
        savedUser.setUsername("martins");
        savedUser.setPassword("encodedPassword");
        savedUser.setProvider("local");
        savedUser.setRole(Role.USER);
        when(repository.findByProviderAndUsername("local", "martins"))
                .thenReturn(Optional.empty());
        when(userMapper.toEntity(dto)).thenReturn(mappedUser);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("martins", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.USER, result.getRole());
        assertEquals("local", result.getProvider());
        verify(repository).findByProviderAndUsername("local", "martins");
        verify(repository).save(any(User.class));
        verify(passwordEncoder).encode("plainPass");

    }

    @Test
    void shouldThrowException_whenUsernameAlreadyTaken() {
        when(repository.findByProviderAndUsername("local", "martins"))
                .thenReturn(Optional.of(mappedUser));

        UsernameAlreadyTakenException exception = assertThrows(
                UsernameAlreadyTakenException.class,
                () -> userService.createUser(dto)
        );

        assertEquals("Username already taken", exception.getMessage());

        verify(repository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).toEntity(any());
    }


    @Test
    void testFindByUsernameOrThrow_Found() {
        when(repository.findByUsername(mappedUser.getUsername())).thenReturn(Optional.of(mappedUser));
        User found = userService.findByUsernameOrThrow(mappedUser.getUsername());
        assertEquals(mappedUser.getUsername(), found.getUsername());
    }

    @Test
    void testFindByUsernameOrThrow_NotFound() {
        when(repository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findByUsernameOrThrow("nouser"));
    }

    @Test
    void testDeleteUserById_Success() {
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);
        assertDoesNotThrow(() -> userService.deleteUserById(1));
    }

    @Test
    void testDeleteUserById_NotFound() {
        when(repository.existsById(2)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(2));
    }

    @Test
    void testUpdateUser() {
        when(repository.findById(1)).thenReturn(Optional.of(mappedUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(repository.save(any(User.class))).thenReturn(mappedUser);
        User updated = new User();
        updated.setUsername("newuser");
        updated.setPassword("newpassword");
        updated.setEmail("new@mail.lv");
        updated.setRole(Role.ADMIN);
        User result = userService.updateUser(1, updated);
        assertEquals("newuser", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals("new@mail.lv", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
    }
} 