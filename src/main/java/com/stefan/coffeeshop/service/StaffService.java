package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.dto.request.StaffRequest;
import com.stefan.coffeeshop.dto.response.StaffResponse;
import com.stefan.coffeeshop.entity.User;
import com.stefan.coffeeshop.exception.BusinessException;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<StaffResponse> listStaff(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return PageResponse.of(
                    userRepository.findByFullNameContainingIgnoreCaseAndActiveTrue(keyword, pageable)
                            .map(StaffResponse::from)
            );
        }
        return PageResponse.of(userRepository.findByActiveTrue(pageable).map(StaffResponse::from));
    }

    public StaffResponse getById(Long id) {
        return StaffResponse.from(findById(id));
    }

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在: " + request.getUsername());
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("新建员工时密码不能为空");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .role(request.getRole())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return StaffResponse.from(userRepository.save(user));
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        User user = findById(id);

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在: " + request.getUsername());
        }

        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        if (request.getActive() != null) user.setActive(request.getActive());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return StaffResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteStaff(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("员工", id));
    }
}
