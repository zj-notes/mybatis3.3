package com.demo.test;

import java.sql.*;

public class JdbcTest {

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String driver = "com.mysql.cj.jdbc.Driver";//1.定义driverClass
            String url = "jdbc:mysql://10.1.32.29:3306/gopher_auth"; //2.定义url
            String username = "gopher_auth";                  //3.定义用户名，写你想要连接到的用户。
            String password = "gopher_auth";                 //4.用户密码。
            String sql = "select * from tbl_sys_role";     //5.你想要查找的表名。
            Class.forName(driver);//6.注册驱动程序
            conn = DriverManager.getConnection(url, username, password);//7.获取数据库连接
            //Statement stmt=conn.createStatement(); //8.构造一个statement对象来执行sql语句
            pstmt = conn.prepareStatement(sql);
            //CallableStatement cstmt = conn.prepareCall("{CALL demoSp(? , ?)}") ;
            rs = pstmt.executeQuery();//9.执行sql
            while (rs.next()) {  //10.遍历结果集
                System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {//11.关闭记录集
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {//12.关闭声明的对象
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {//13.关闭连接
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
