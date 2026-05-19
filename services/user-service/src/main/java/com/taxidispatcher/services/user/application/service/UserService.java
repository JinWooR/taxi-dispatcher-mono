package com.taxidispatcher.services.user.application.service;

import com.taxidispatcher.services.user.application.dto.response.UserProfileResponse;
import com.taxidispatcher.services.user.domain.user.User;
import com.taxidispatcher.services.user.domain.user.UserId;
import com.taxidispatcher.services.user.domain.user.UserRepository;
import com.taxidispatcher.shared.common.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 사용자 프로필 비즈니스 로직
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse registerProfile(String accountId, String name, String phone) {
        if (userRepository.existsByAccountId(accountId)) {
            throw new DomainException("USER_DUPLICATE_ACCOUNT", "이미 등록된 프로필입니다", HttpStatus.CONFLICT);
        }

        User user = new User(UserId.generate(), accountId, name, phone);
        User saved = userRepository.save(user);

        return UserProfileResponse.from(saved);
    }

    public UserProfileResponse getMyProfile(String accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        return UserProfileResponse.from(user);
    }

    public UserProfileResponse updateMyProfile(String accountId, String name, String phone) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        user.update(name, phone);
        User updated = userRepository.save(user);

        return UserProfileResponse.from(updated);
    }

    public void deleteMyProfile(String accountId) {
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        user.delete();
        userRepository.save(user);
    }
}
