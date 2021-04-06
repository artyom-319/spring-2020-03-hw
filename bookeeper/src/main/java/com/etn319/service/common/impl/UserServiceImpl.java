package com.etn319.service.common.impl;

import com.etn319.dao.mongo.UserMongoRepository;
import com.etn319.model.ServiceUser;
import com.etn319.service.common.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMongoRepository repository;

    @Override
    public Optional<ServiceUser> loadCurrentAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            String username = auth.getName();
            return repository.findByName(username);
        }
        return Optional.empty();
    }

    @Override
    public List<ServiceUser> getAllWithRoles() {
        return repository.findAllWithRoles();
    }

    @Override
    public List<ServiceUser> updateUserRoles(List<ServiceUser> users) {
        return repository.updateUserRoles(users);
    }
}
