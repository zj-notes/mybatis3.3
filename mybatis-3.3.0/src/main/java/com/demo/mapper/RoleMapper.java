package com.demo.mapper;

import com.demo.po.TblSysRole;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface RoleMapper {
    // 只有一个参数
    TblSysRole getRole(String id);

    // 使用@Param
    TblSysRole getRoleByParam(@Param("id") String id);

    // 多个参数，不用@Param
    TblSysRole getRoleByIdAndName(String id, String roleName);

    // 多个参数，map传参
    TblSysRole getRoleByIdAndNameByMap(Map<String, String> map);

    // 多个参数，用@Param
    TblSysRole getRoleByIdAndNameByParam(@Param("id") String id, @Param("roleName") String roleName);

    // 使用java bean，不加 @Param，sql中可以直接使用 bean 的属性#{id}
    TblSysRole getRoleByBean(TblSysRole role);

    // 使用java bean 和 @Param，sql中要使用 #{role.id}
    TblSysRole getRoleByBean2(@Param("role") TblSysRole role);

    TblSysRole findRole(String roleName);

    int deleteRole(String id);
    int insertRole(TblSysRole role);
}
