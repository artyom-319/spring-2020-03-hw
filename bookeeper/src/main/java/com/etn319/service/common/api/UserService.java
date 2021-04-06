package com.etn319.service.common.api;

import com.etn319.model.ServiceUser;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<ServiceUser> loadCurrentAuthenticatedUser();

    List<ServiceUser> getAllWithRoles();

    List<ServiceUser> updateUserRoles(List<ServiceUser> users);
}
