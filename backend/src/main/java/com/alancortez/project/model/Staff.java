package com.alancortez.project.model;

import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.PRIVILEGES;
import com.alancortez.project.utils.USER_ROLE;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Recommended for easier object creation
import java.util.Date; // Required for the inherited Date fields
import java.util.UUID;

@Entity
@Table(name = "staff")
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields (recommended)
public class Staff extends User {
    @Column(name = "staff_id", unique = true, nullable = false)
    private String staffID;

    // --- Private Field (Excluded from automatic JPA mapping) ---
    // If Privilege is a simple transient field not meant for the database:
    @Column(name = "privilege", unique = false, nullable = false)
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

    public void togglePrivilege(PRIVILEGES privilegeToUpdate, String adminID) {
        UserService userService = new UserService();

        User admin = userService.getUserByAdminID(adminID);

        if (admin != null) {
            switch(privilegeToUpdate) {
                case CREATE_RECIPE:
                    this.privilege.setCanCreateRecipe(!this.privilege.getCanCreateRecipe());
                    break;
                case READ_RECIPE:
                    this.privilege.setCanReadRecipe(!this.privilege.getCanReadRecipe());
                    break;
                case UPDATE_RECIPE:
                    this.privilege.setCanUpdateRecipe(!this.privilege.getCanUpdateRecipe());
                    break;
                case DELETE_RECIPE:
                    this.privilege.setCanDeleteRecipe(!this.privilege.getCanDeleteRecipe());
                    break;
                case CREATE_INGREDIENT:
                    this.privilege.setCanCreateIngredient(!this.privilege.getCanCreateIngredient());
                    break;
                case READ_INGREDIENT:
                    this.privilege.setCanReadIngredient(!this.privilege.getCanReadIngredient());
                    break;
                case UPDATE_INGREDIENT:
                    this.privilege.setCanUpdateIngredient(!this.privilege.getCanUpdateIngredient());
                    break;
                case DELETE_INGREDIENT:
                    this.privilege.setCanDeleteIngredient(!this.privilege.getCanDeleteIngredient());
                    break;
            }
            this.dateUpdated = new Date();
        }

    }

    public String getStaffID() {
        return staffID;
    }
}
