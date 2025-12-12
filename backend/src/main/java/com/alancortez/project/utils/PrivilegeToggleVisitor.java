package com.alancortez.project.utils;

import com.alancortez.project.model.Admin;
import com.alancortez.project.model.Privilege;
import com.alancortez.project.model.Staff;

public class PrivilegeToggleVisitor implements UserActionVisitor{
    private final PRIVILEGES[] privilegesToToggle;
    private final String targetStaffID;
    private Admin actingAdmin;

    public PrivilegeToggleVisitor(PRIVILEGES[] privilegesToToggle, String targetStaffID) {
        this.privilegesToToggle = privilegesToToggle;
        this.targetStaffID = targetStaffID;
    }

    public void visit(Staff staff) {
        if (staff.getStaffID().equals(this.targetStaffID) && this.actingAdmin != null) {
            Privilege privilege = staff.getPrivilege();

            for (PRIVILEGES privilegeToUpdate : privilegesToToggle) {
                switch(privilegeToUpdate) {
                    case CREATE_RECIPE:
                        privilege.setCanCreateRecipe(!privilege.getCanCreateRecipe());
                        break;
                    case READ_RECIPE:
                        privilege.setCanReadRecipe(!privilege.getCanReadRecipe());
                        break;
                    case UPDATE_RECIPE:
                        privilege.setCanUpdateRecipe(!privilege.getCanUpdateRecipe());
                        break;
                    case DELETE_RECIPE:
                        privilege.setCanDeleteRecipe(!privilege.getCanDeleteRecipe());
                        break;
                    case CREATE_INGREDIENT:
                        privilege.setCanCreateIngredient(!privilege.getCanCreateIngredient());
                        break;
                    case READ_INGREDIENT:
                        privilege.setCanReadIngredient(!privilege.getCanReadIngredient());
                        break;
                    case UPDATE_INGREDIENT:
                        privilege.setCanUpdateIngredient(!privilege.getCanUpdateIngredient());
                        break;
                    case DELETE_INGREDIENT:
                        privilege.setCanDeleteIngredient(!privilege.getCanDeleteIngredient());
                        break;
                }
            }

            staff.setPrivilege(privilege);
            staff.setDateUpdated();
        }
    }
    public void visit(Admin admin) {
        this.actingAdmin = admin;
    }
}
