package com.etn319.dao.mongo;

import com.etn319.model.ServiceUser;

import java.util.List;

public interface UserMongoRepositoryCustom {
    List<ServiceUser> updateUserRoles(List<ServiceUser> users);

    List<ServiceUser> findAllWithRoles();
}
