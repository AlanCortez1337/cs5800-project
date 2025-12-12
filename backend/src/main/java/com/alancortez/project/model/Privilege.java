package com.alancortez.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Privilege {
    @Column(name = "can_create_ingredient", nullable = false)
    private Boolean canCreateIngredient = false;

    @Column(name = "can_read_ingredient", nullable = false)
    private Boolean canReadIngredient = false;

    @Column(name = "can_update_ingredient", nullable = false)
    private Boolean canUpdateIngredient = false;

    @Column(name = "can_delete_ingredient", nullable = false)
    private Boolean canDeleteIngredient = false;

    @Column(name = "can_create_recipe", nullable = false)
    private Boolean canCreateRecipe = false;

    @Column(name = "can_read_recipe", nullable = false)
    private Boolean canReadRecipe = false;

    @Column(name = "can_update_recipe", nullable = false)
    private Boolean canUpdateRecipe = false;

    @Column(name = "can_delete_recipe", nullable = false)
    private Boolean canDeleteRecipe = false;

    private Privilege(PrivilegeBuilder builder) {
        canCreateIngredient = builder.canCreateIngredient;
        canReadIngredient = builder.canReadIngredient;
        canUpdateIngredient = builder.canUpdateIngredient;
        canDeleteIngredient = builder.canDeleteIngredient;
        canCreateRecipe = builder.canCreateRecipe;
        canReadRecipe = builder.canReadRecipe;
        canUpdateRecipe = builder.canUpdateRecipe;
        canDeleteRecipe = builder.canDeleteRecipe;
    }

    public static class PrivilegeBuilder {
        private Boolean canCreateIngredient = false;
        private Boolean canReadIngredient = false;
        private Boolean canUpdateIngredient = false;
        private Boolean canDeleteIngredient = false;
        private Boolean canCreateRecipe = false;
        private Boolean canReadRecipe = false;
        private Boolean canUpdateRecipe = false;
        private Boolean canDeleteRecipe = false;

        public PrivilegeBuilder() {}

        public PrivilegeBuilder allowCreateIngredient() {
            this.canCreateIngredient = true;
            return this;
        }
        public PrivilegeBuilder allowReadIngredient() {
            this.canReadIngredient = true;
            return this;
        }
        public PrivilegeBuilder allowUpdateIngredient() {
            this.canUpdateIngredient = true;
            return this;
        }
        public PrivilegeBuilder allowDeleteIngredient() {
            this.canDeleteIngredient = true;
            return this;
        }
        public PrivilegeBuilder allowCreateRecipe() {
            this.canCreateRecipe = true;
            return this;
        }
        public PrivilegeBuilder allowReadRecipe() {
            this.canReadRecipe = true;
            return this;
        }
        public PrivilegeBuilder allowUpdateRecipe() {
            this.canUpdateRecipe = true;
            return this;
        }
        public PrivilegeBuilder allowDeleteRecipe() {
            this.canDeleteRecipe = true;
            return this;
        }

        public Privilege build() {
            return new Privilege(this);
        }
    }

    public Boolean getCanCreateIngredient() {
        return canCreateIngredient;
    }

    public Boolean getCanReadIngredient() {
        return canReadIngredient;
    }

    public Boolean getCanUpdateIngredient() {
        return canUpdateIngredient;
    }

    public Boolean getCanDeleteIngredient() {
        return canDeleteIngredient;
    }

    public Boolean getCanCreateRecipe() {
        return canCreateRecipe;
    }

    public Boolean getCanReadRecipe() {
        return canReadRecipe;
    }

    public Boolean getCanUpdateRecipe() {
        return canUpdateRecipe;
    }

    public Boolean getCanDeleteRecipe() {
        return canDeleteRecipe;
    }

    public void setCanCreateIngredient(Boolean canCreateIngredient) {
        this.canCreateIngredient = canCreateIngredient;
    }

    public void setCanReadIngredient(Boolean canReadIngredient) {
        this.canReadIngredient = canReadIngredient;
    }

    public void setCanUpdateIngredient(Boolean canUpdateIngredient) {
        this.canUpdateIngredient = canUpdateIngredient;
    }

    public void setCanDeleteIngredient(Boolean canDeleteIngredient) {
        this.canDeleteIngredient = canDeleteIngredient;
    }

    public void setCanCreateRecipe(Boolean canCreateRecipe) {
        this.canCreateRecipe = canCreateRecipe;
    }

    public void setCanReadRecipe(Boolean canReadRecipe) {
        this.canReadRecipe = canReadRecipe;
    }

    public void setCanUpdateRecipe(Boolean canUpdateRecipe) {
        this.canUpdateRecipe = canUpdateRecipe;
    }

    public void setCanDeleteRecipe(Boolean canDeleteRecipe) {
        this.canDeleteRecipe = canDeleteRecipe;
    }
}
