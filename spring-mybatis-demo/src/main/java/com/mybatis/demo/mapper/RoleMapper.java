package com.mybatis.demo.mapper;

import com.mybatis.demo.po.Role;

public interface RoleMapper {
    public Role getRole(Long id);
    public Role findRole(String roleName);
    public int deleteRole(Long id);
    public int insertRole(Role role);
}
