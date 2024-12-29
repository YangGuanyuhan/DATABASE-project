package io.pubmed.service.impl;

import io.pubmed.service.DatabaseService;
import io.pubmed.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeywordServiceImpl implements KeywordService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    public int[] getArticleCountByKeywordInPastYears(String keyword) {

            String sql = """
        WITH RECURSIVE years AS (
            SELECT EXTRACT(YEAR FROM MIN(a.date_created)) AS year
            FROM article a
            JOIN article_keywords ak ON a.id = ak.article_id
            JOIN keywords k ON ak.keyword_id = k.id
            WHERE k.keyword = ?
            GROUP BY k.keyword
            UNION ALL
            SELECT year + 1
            FROM years
            WHERE year < EXTRACT(YEAR FROM CURRENT_DATE)
        ),
        keyword_articles AS (
            SELECT DISTINCT a.id, EXTRACT(YEAR FROM a.date_created) as article_year
            FROM article a
            JOIN article_keywords ak ON a.id = ak.article_id
            JOIN keywords k ON ak.keyword_id = k.id
            WHERE k.keyword = ?
        ),
        year_counts AS (
            SELECT y.year,
                   COALESCE(COUNT(DISTINCT ka.id), 0) as article_count
            FROM years y
            LEFT JOIN keyword_articles ka ON y.year = ka.article_year
            GROUP BY y.year
            ORDER BY y.year ASC
        )
        SELECT article_count
        FROM year_counts
    """;

            List<Integer> articleCounts = new ArrayList<>();
            try (Connection conn = databaseService.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, keyword);
                stmt.setString(2, keyword);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int count = rs.getInt("article_count");
                        if (count > 0) {  // Only add counts greater than 0
                            articleCounts.add(count);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return new int[0]; // Return an empty array on error
            }

            // Convert the list to an array and return
            return articleCounts.stream().mapToInt(i -> i).toArray();
        }


    }
