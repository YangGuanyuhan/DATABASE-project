package io.pubmed.service.impl;

import io.pubmed.dto.User;
import io.pubmed.dto.Journal;
import io.pubmed.service.Users;
import io.pubmed.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

@Service

public class UserServicelmpl implements Users {
    @Autowired
    private DataSource dataSource;

    @Override
    public boolean insertAccount(int userId,String username, String password) {

        String sql = "INSERT INTO account (id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, username); // 假设用户名为 "user" + userId
            stmt.setString(3, password);


            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // 如果插入成功，返回 true
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 如果发生异常或插入失败，返回 false
        }
    }

    @Override
    public int findFirstDigitOfUserId(int userId,String usename, String password) {

        String sql = "SELECT id FROM account WHERE id = ? AND password = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userIdString = String.valueOf(rs.getInt("id"));
                    return Integer.parseInt(userIdString.substring(0, 1)); // 返回 userId 的首个数字
                } else {
                    return 0; // 没有找到匹配的记录，返回 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // 查询过程中发生异常时，返回 0
        }
    }
}
