package com.neo.tech.whatsappPoc.user.service;


import com.neo.tech.whatsappPoc.common.exception.UnauthorizedActionException;
import com.neo.tech.whatsappPoc.executor.entity.ExecutorEntity;
import com.neo.tech.whatsappPoc.user.entity.ManagerEntity;
import com.neo.tech.whatsappPoc.user.entity.UserEntity;
import com.neo.tech.whatsappPoc.executor.repository.ExecutorRepository;
import com.neo.tech.whatsappPoc.user.repository.ManagerRepository;
import com.neo.tech.whatsappPoc.user.repository.UserRepository;
import com.neo.tech.whatsappPoc.util.UserRole;
import com.neo.tech.whatsappPoc.util.UserStatus;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final ExecutorRepository executorRepository;

    /**
     * COMMAND method
     * Ensures user exists and is authorized.
     */
    public void resolveUserByMobile(String mobileNumber) {

        if (mobileNumber == null || mobileNumber.isBlank()) {
            log.warn("Mobile number missing in request");
            throw new UnauthorizedActionException("Mobile number is missing");
        }

        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseGet(() -> {
                    log.info("Creating new user for mobile={}", mobileNumber);
                    return createUserShell(mobileNumber);
                });

        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Inactive user attempted access: mobile={}", mobileNumber);
            throw new UnauthorizedActionException("User is inactive");
        }

        resolveAndSetRole(user);
    }


    private UserEntity createUserShell(String mobileNumber) {
        return userRepository.save(
                UserEntity.builder()
                        .mobileNumber(mobileNumber)
                        .status(UserStatus.ACTIVE)
                        .build()
        );
    }
    @Transactional(readOnly = true)
    public UserRole getUserRole(String mobileNumber) {

        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new UnauthorizedActionException("Mobile number is missing");
        }

        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() ->
                        new UnauthorizedActionException("User not found")
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedActionException("User is inactive");
        }

        if (user.getRole() == null) {
            throw new UnauthorizedActionException("User role not resolved");
        }

        return user.getRole();
    }

    private void resolveAndSetRole(UserEntity user) {

        Long userId = user.getId();

        boolean isManager = managerRepository.findByUserId(userId).isPresent();
        boolean isExecutor = executorRepository.findByUserId(userId).isPresent();

        if (isManager && isExecutor) {
            throw new UnauthorizedActionException(
                    "User cannot be both manager and executor"
            );
        }

        if (!isManager && !isExecutor) {
            throw new UnauthorizedActionException(
                    "User is not authorized"
            );
        }

        UserRole resolvedRole =
                isManager ? UserRole.MANAGER : UserRole.EXECUTOR;

        if (user.getRole() != resolvedRole) {
            user.setRole(resolvedRole);
            userRepository.save(user);
        }
    }
    @Transactional
    public void createManager(String mobile, String name) {


        UserEntity user = userRepository.findByMobileNumber(mobile)
                .orElseGet(() -> createUser(mobile, UserRole.MANAGER));

        if (managerRepository.existsByUserId(user.getId())) {
            throw new ValidationException("Manager already exists for this mobile");
        }

        ManagerEntity manager = ManagerEntity.builder()
                .user(user)
                .name(name.trim())
                .build();

        managerRepository.save(manager);

        user.setRole(UserRole.MANAGER);
        userRepository.save(user);

        log.info("Manager created: mobile={}", mobile);
    }

    // ---------------- EXECUTOR ----------------

    @Transactional
    public void createExecutor(
            String mobile,
            String name,
            String branchCode,
            String branchName
    ) {


        if (branchCode == null || branchCode.isBlank() || branchName == null || branchName.isBlank()) {
            throw new ValidationException("Branch  is required");
        }


        UserEntity user = userRepository.findByMobileNumber(mobile)
                .orElseGet(() -> createUser(mobile , UserRole.EXECUTOR));

        if (executorRepository.existsByUserId(user.getId())) {
            throw new ValidationException("Executor already exists for this mobile");
        }

        ExecutorEntity executor = ExecutorEntity.builder()
                .user(user)
                .name(name.trim())
                .branchCode(branchCode.trim())
                .branchName(branchName.trim())
                .build();

        executorRepository.save(executor);

        user.setRole(UserRole.EXECUTOR);
        userRepository.save(user);

        log.info("Executor created: mobile={}, branch={}", mobile, branchCode);
    }

    // ---------------- COMMON ----------------

    private UserEntity createUser(String mobile ,UserRole role) {
        return userRepository.save(
                UserEntity.builder()
                        .mobileNumber(mobile.trim())
                        .role(role)
                        .status(UserStatus.ACTIVE)
                        .build()
        );
    }


}

