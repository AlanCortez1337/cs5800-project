package com.alancortez.project.utils;

import com.alancortez.project.model.Admin;
import com.alancortez.project.model.Staff;
import com.alancortez.project.model.User;

public class UserFactory {
    private static UserFactory instance = null;

    private UserFactory() {}

    public static UserFactory getInstance() {
        if (instance == null) {
            return new UserFactory();
        }
        return instance;
    }

    public User createUser(String userName, String password, USER_ROLE role) {
        switch(role) {
            case STAFF:
                return new Staff(userName, password, role);
            case ADMIN:
                return new Admin(userName, password, role);
            default:
                return null;
        }
    }
}
