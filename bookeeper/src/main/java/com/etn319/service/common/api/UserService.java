package com.etn319.service.common.api;

import com.etn319.model.ServiceUser;

import java.util.Optional;

public interface UserService {
    Optional<ServiceUser> loadCurrentAuthenticatedUser();
}
