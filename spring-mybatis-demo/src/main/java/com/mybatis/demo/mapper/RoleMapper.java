
package com.mybatis.demo.mapper;


import com.mybatis.demo.po.TblSysRole;

public interface RoleMapper {
    public TblSysRole getRole(String id);
    public TblSysRole findRole(String roleName);
    public int deleteRole(String id);
    public int insertRole(TblSysRole role);
}
