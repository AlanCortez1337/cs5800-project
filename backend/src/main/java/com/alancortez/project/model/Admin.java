package com.alancortez.project.model;

import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.PRIVILEGES;
import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserActionVisitor;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "admin")
@NoArgsConstructor
public class Admin extends User {
    @Column(name = "admin_id", unique = true, nullable = false)
    private String adminID;

    public Admin(
            String username,
            String password,
            USER_ROLE role
    ) {
        super(username, password, role);
        this.adminID = UUID.randomUUID().toString();
    }

    public String getAdminID() {
        return adminID;
    }

    public void accept(UserActionVisitor visitor) {
        visitor.visit(this);
    }
}
