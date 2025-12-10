package com.alancortez.project.utils;

import com.alancortez.project.model.Admin;
import com.alancortez.project.model.Staff;

public interface UserActionVisitor {
    void visit(Staff staff);
    void visit(Admin admin);
}
