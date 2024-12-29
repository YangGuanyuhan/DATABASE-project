package io.pubmed.service.impl;

import io.pubmed.dto.Journal;
import io.pubmed.service.DatabaseService;
import io.pubmed.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    public double getImpactFactor(String journal_id, int year) {
        String sql = """
            WITH journal_articles AS (
                SELECT a.id
                FROM article a
                JOIN article_journal aj ON a.id = aj.article_id
                JOIN journal j ON aj.journal_id = j.id
                WHERE j.id = ?
                AND (EXTRACT(year FROM date_completed) = ? OR EXTRACT(year FROM date_completed) = ?)
            ),
            citations AS (
                SELECT COUNT(*) as citation_count
                FROM article_references ar
                JOIN article a ON ar.article_id = a.id
                JOIN journal_articles ja ON ar.reference_id = ja.id
                WHERE EXTRACT(YEAR FROM a.date_completed) = ?
            ),
            article_count AS (
                SELECT COUNT(*) as total_articles
                FROM journal_articles
            )
            SELECT CASE 
                WHEN ac.total_articles = 0 THEN 0
                ELSE CAST(c.citation_count AS FLOAT) / ac.total_articles
            END as impact_factor
            FROM citations c, article_count ac
        """;

        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, journal_id);
            stmt.setInt(2, year - 2);
            stmt.setInt(3, year - 1);
            stmt.setInt(4, year);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("impact_factor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public boolean updateJournalName(Journal journal, int year, String new_name, String new_id) {
        String getJournalSql = """
            SELECT issn, country FROM journal WHERE id = ?
        """;

        String insertNewJournalSql = """
            INSERT INTO journal (id, title, issn, country)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title
            RETURNING id
        """;

        String updateArticleJournalSql = """
            WITH articles_to_update AS (
                SELECT DISTINCT aj.article_id
                FROM article_journal aj
                JOIN article a ON a.id = aj.article_id
                WHERE aj.journal_id = ?
                AND EXTRACT(YEAR FROM a.date_created) >= ?
            )
            UPDATE article_journal aj
            SET journal_id = ?
            WHERE article_id IN (SELECT article_id FROM articles_to_update)
        """;

        try (Connection conn = databaseService.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Get original ISSN and country
                String issn = null;
                String country = null;
                try (PreparedStatement stmt = conn.prepareStatement(getJournalSql)) {
                    stmt.setString(1, journal.getId());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        issn = rs.getString("issn");
                        country = rs.getString("country");
                    }
                }

                // Insert or update new journal
                try (PreparedStatement stmt = conn.prepareStatement(insertNewJournalSql)) {
                    stmt.setString(1, new_id);
                    stmt.setString(2, new_name);
                    stmt.setString(3, issn);
                    stmt.setString(4, country);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                }

                // Update article_journal relations
                try (PreparedStatement stmt = conn.prepareStatement(updateArticleJournalSql)) {
                    stmt.setString(1, journal.getId());
                    stmt.setInt(2, year);
                    stmt.setString(3, new_id);
                    stmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
