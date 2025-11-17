package com.alancortez.project.model;

import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.PRIVILEGES;
import com.alancortez.project.utils.USER_ROLE;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "admin")
@NoArgsConstructor
public class Admin extends User {
    @Column(name = "admin_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminID;

    public Admin(
            String username,
            String password,
            USER_ROLE role
    ) {
        super(username, password, role);
    }

    public Long getAdminID() {
        return adminID;
    }

    public void setPrivilege(PRIVILEGES privilegeToUpdate, Long staffID) {
        UserService userService = new UserService();

        Staff staff = (Staff) userService.getUserByStaffID(staffID);

        if (staff != null) {
            this.dateUpdated = new Date();
            staff.togglePrivilege(privilegeToUpdate, this.getAdminID());
        }

    }
}
