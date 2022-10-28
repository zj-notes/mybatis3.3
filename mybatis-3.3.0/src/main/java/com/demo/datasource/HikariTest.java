package com.demo.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.UUID;

public class HikariTest {
    public static void main(String[] args) {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://10.1.32.29:3306/gopher_auth");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setUsername("gopher_auth");
        hikariConfig.setPassword("gopher_auth");

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = dataSource.getConnection();
            String sql = "insert into tbl_sys_role (id, role_name, role_code, system_code) values (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            String id = UUID.randomUUID().toString().replace("-", "");  // 41a68e9895b0437b9d35792c85565a96
            pstmt.setString(1, id);
            pstmt.setString(2, "管理员" + id);
            pstmt.setString(3, "admin" + id);
            pstmt.setString(4, "gims");

            conn.setAutoCommit(false);
            pstmt.executeUpdate();
            conn.commit();

            pstmt = conn.prepareStatement("select * from tbl_sys_role where id = ?");
            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
