package com.ticketsystem.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketsystem.entity.User;
import com.ticketsystem.entity.UserRole;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.exception.UserAlreadyExistsException;
import com.ticketsystem.repository.UserRepository;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User createUser(String email, String password, String firstName, String lastName, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists with email: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role != null ? role : UserRole.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        logger.info("Created new user with email: {}", email);
        return savedUser;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getActiveUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable);
    }

    public Page<User> searchUsers(String search, Pageable pageable) {
        return userRepository.findActiveUsersBySearch(search, pageable);
    }

    public Page<User> getUsersWithFilters(UserRole role, Boolean isActive, String search, Pageable pageable) {
        return userRepository.findUsersWithFilters(role, isActive, search, pageable);
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRoleAndIsActiveTrue(role);
    }

    public List<User> getSupportAgents() {
        return userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_AGENT);
    }

    public List<User> getAdmins() {
        return userRepository.findByRoleAndIsActiveTrue(UserRole.ADMIN);
    }

    public User updateUser(UUID id, String firstName, String lastName, String email) {
        User user = getUserById(id);

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists with email: " + email);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        User updatedUser = userRepository.save(user);
        logger.info("Updated user with id: {}", id);
        return updatedUser;
    }

    public User updateUserRole(UUID id, UserRole role) {
        User user = getUserById(id);
        user.setRole(role);

        User updatedUser = userRepository.save(user);
        logger.info("Updated user role for id: {} to {}", id, role);
        return updatedUser;
    }

    public User updateUserPassword(UUID id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));

        User updatedUser = userRepository.save(user);
        logger.info("Updated password for user with id: {}", id);
        return updatedUser;
    }

    public User deactivateUser(UUID id) {
        User user = getUserById(id);
        user.setIsActive(false);

        User updatedUser = userRepository.save(user);
        logger.info("Deactivated user with id: {}", id);
        return updatedUser;
    }

    public User activateUser(UUID id) {
        User user = getUserById(id);
        user.setIsActive(true);

        User updatedUser = userRepository.save(user);
        logger.info("Activated user with id: {}", id);
        return updatedUser;
    }

    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        logger.info("Deleted user with id: {}", id);
    }

    public long getUserCountByRole(UserRole role) {
        return userRepository.countByRoleAndIsActiveTrue(role);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public long getCreatedTicketCount(User user) {
        // This will be implemented when we have access to TicketRepository
        // For now, return 0 as placeholder
        return 0L;
    }

    public long getAssignedTicketCount(User user) {
        // This will be implemented when we have access to TicketRepository
        // For now, return 0 as placeholder
        return 0L;
    }
}
