package io.pubmed.service.impl;

import io.pubmed.service.DatabaseService;
import io.pubmed.service.GrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrantServiceImpl implements GrantService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    public int[] getCountryFundPapers(String country) {
        String sql = """
            SELECT DISTINCT a.id
            FROM article a
            JOIN article_grants ag ON a.id = ag.article_id
            JOIN grant_info g ON ag.grant_id = g.id
            WHERE g.country = ?
            ORDER BY a.id
        """;

        List<Integer> result = new ArrayList<>();
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, country);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
