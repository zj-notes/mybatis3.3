package com.demo.jdbc;

import com.mysql.cj.jdbc.NonRegisteringDriver;

import java.sql.*;
import java.util.Properties;

public class JdbcTest2 {

    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://10.1.32.29:3306/gopher_auth";
            Properties info = new Properties();
            info.put("user", "gopher_auth");
            info.put("password", "gopher_auth");
            String sql = "select * from tbl_sys_role";

            NonRegisteringDriver nonRegisteringDriver = new NonRegisteringDriver();
            java.sql.Connection conn =  nonRegisteringDriver.connect(url, info);

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
