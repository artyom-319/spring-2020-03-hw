package com.etn319.dao.mongo;

import com.etn319.model.ServiceUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Slf4j
public class UserMongoRepositoryCustomImpl implements UserMongoRepositoryCustom {
    private final MongoTemplate template;

    public UserMongoRepositoryCustomImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public List<ServiceUser> updateUserRoles(List<ServiceUser> users) {
        List<ServiceUser> updated = new ArrayList<>();
        for (var user : users) {
            Query query = new Query().addCriteria(Criteria.where("_id").is(user.getId()));
            Update update = new Update().set("authorities", user.getAuthorities());
            ServiceUser updatedUser = template.findAndModify(query, update, ServiceUser.class);
            updated.add(updatedUser);
        }
        return updated;
    }

    @Override
    public List<ServiceUser> findAllWithRoles() {
        Aggregation aggregation = Aggregation.newAggregation(
                project().andInclude("name", "authorities")
        );
        return template.aggregate(aggregation, ServiceUser.class, ServiceUser.class)
                .getMappedResults();
    }
}
