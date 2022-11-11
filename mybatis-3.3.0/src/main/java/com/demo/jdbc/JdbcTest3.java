package com.demo.jdbc;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.result.ResultSetImpl;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcTest3 {

    public static void main(String[] args) {

        ConnectionImpl conn = null;
        PreparedStatement stmt = null;
        ResultSetImpl rs = null;
        try {
            // 注册驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            // 获取链接
            conn = (ConnectionImpl) DriverManager.getConnection("jdbc:mysql://10.1.32.29:3306/gopher_auth", "gopher_auth", "gopher_auth");
            conn.setAutoCommit(false);
            // 获取 Statement
            stmt = conn.prepareStatement("insert into tbl_sys_role (id, role_name, role_code, system_code) values (?, ?, ?, ?)");
            // 设置参数
            stmt.setString(1, UUID.randomUUID().toString().replace("-", ""));
            stmt.setString(2, "管理员");
            stmt.setString(3, "admin");
            stmt.setString(4, "gims");
            // 执行SQL
            stmt.executeUpdate();
            // 提交事务
            conn.commit();

            stmt = conn.prepareStatement("select * from tbl_sys_role where id = ?");
            stmt.setString(1, "41a68e9895b0437b9d35792c85565a96");
            rs = (ResultSetImpl) stmt.executeQuery();

            while (rs.next()) {
                System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 清理资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
