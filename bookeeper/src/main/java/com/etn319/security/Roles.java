package com.etn319.security;

import java.util.List;

public class Roles {
    public static final String ROLE_CAN_UPDATE = "ROLE_CAN_UPDATE";
    public static final String ROLE_CAN_DELETE = "ROLE_CAN_DELETE";
    public static final String ROLE_CAN_COMMENT = "ROLE_CAN_COMMENT";
    public static final String ROLE_CAN_ADMINISTER = "ROLE_CAN_ADMINISTER";

    public static List<String> all() {
        return List.of(
                ROLE_CAN_UPDATE,
                ROLE_CAN_DELETE,
                ROLE_CAN_COMMENT,
                ROLE_CAN_ADMINISTER
        );
    }
}
