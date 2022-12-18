package com.style.approval.auth.service;

import com.style.approval.auth.dto.SignupDto;
import com.style.approval.exception.ServiceException;
import com.style.approval.enums.ErrorCode;
import com.style.approval.user.entity.UserEntity;
import com.style.approval.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean signup(SignupDto.Request request) {
        boolean existUser = userRepository.existsByUsername(request.getUsername().toLowerCase());

        if (existUser)  throw new ServiceException(ErrorCode.USER_DUP);

        UserEntity user = new UserEntity(request);
        user.encryptPassword(passwordEncoder);

        userRepository.save(user);

        return user.getId() != null;
    }
}
