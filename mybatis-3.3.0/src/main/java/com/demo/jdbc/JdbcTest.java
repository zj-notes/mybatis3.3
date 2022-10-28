package com.demo.jdbc;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.result.ResultSetImpl;

import java.sql.*;
import java.util.Random;
import java.util.UUID;

public class JdbcTest {

    public static void main(String[] args) {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;

        ConnectionImpl conn = null;
        Statement stmt = null;
        ClientPreparedStatement pstmt = null;
        ResultSetImpl rs = null;
        try {
            com.mysql.cj.jdbc.Driver dr = new Driver();

            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://10.1.32.29:3306/gopher_auth";
            String username = "gopher_auth";
            String password = "gopher_auth";

            // 注册驱动程序，三种注册方式
            // JDBC4.0后的规范，在jar包中设置了META-INF/services/jdbc.sql.Driver文件，并且写入了驱动，那么会自动加载
            // 如果是之前的，则需要使用Class.forName()进行显式加载。
            // 自动加载依赖ServiceLoader.load(Driver.class);JDBC 4.0 Drivers 必须包括 META-INF/services/java.sql.Driver 文件，
            // DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            // Class.forName("com.mysql.jdbc.Driver");
            // DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            // System.setProperty("jdbc.drivers","com.mysql.jdbc.Driver");

            conn = (ConnectionImpl) DriverManager.getConnection(url, username, password);

            // Statement statement = conn.createStatement();
            // Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            // Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
            // ResultSet resultSet = statement.executeQuery("select * from tbl_sys_role");

            // stmt = conn.createStatement();

            String sql = "insert into tbl_sys_role (id, role_name, role_code, system_code) values (?, ?, ?, ?)";
            pstmt = (ClientPreparedStatement) conn.prepareStatement(sql);
            String id = UUID.randomUUID().toString().replace("-", "");  // 41a68e9895b0437b9d35792c85565a96
            pstmt.setString(1, id);
            pstmt.setString(2, "管理员" + id);
            pstmt.setString(3, "admin" + id);
            pstmt.setString(4, "gims");

            conn.setAutoCommit(false);
            pstmt.executeUpdate();
            conn.commit();

            pstmt = (ClientPreparedStatement) conn.prepareStatement("select * from tbl_sys_role where id = ?");
            pstmt.setString(1, id);

            // CallableStatement cstmt = conn.prepareCall("{CALL demoSp(? , ?)}");
            rs = (ResultSetImpl) pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {// 关闭记录集
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {// 关闭声明的对象
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {// 关闭连接
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
