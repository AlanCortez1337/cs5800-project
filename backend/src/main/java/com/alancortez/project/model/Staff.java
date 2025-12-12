package com.alancortez.project.model;

import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserActionVisitor;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "staff")
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends User {
    @Column(name = "staff_id", unique = true, nullable = false)
    private String staffID;

    @Embedded
    private Privilege privilege;

    public Staff(
            String username,
            String password,
            USER_ROLE role
    ) {
        super(username, password, role);

        this.privilege = new Privilege.PrivilegeBuilder().allowReadIngredient().allowReadRecipe().allowUpdateIngredient().build();
        this.staffID = UUID.randomUUID().toString();
    }

    public Privilege getPrivilege() { return this.privilege; }
    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public void accept(UserActionVisitor visitor) {
        visitor.visit(this);
    }

    public String getStaffID() {
        return staffID;
    }
    public void setDateUpdated() {
        this.dateUpdated = new Date();
    }
}
