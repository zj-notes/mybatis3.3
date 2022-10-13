package com.demo.mapper;

import com.demo.po.TblSysRole;

public interface RoleMapper {
    public TblSysRole getRole(String id);
    public TblSysRole findRole(String roleName);
    public int deleteRole(String id);
    public int insertRole(TblSysRole role);
}
