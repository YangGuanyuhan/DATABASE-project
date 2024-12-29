package io.pubmed.service.impl;

import io.pubmed.dto.Article;
import io.pubmed.dto.Journal;
import io.pubmed.service.ArticleService;
import io.pubmed.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Calendar;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    public int getArticleCitationsByYear(int id, int year) {
        String sql = "SELECT COUNT(*) FROM article_references ar " +
                "JOIN article a ON ar.article_id = a.id " +
                "WHERE ar.reference_id = ? AND EXTRACT(YEAR FROM a.date_created) = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String formatISSN(String issn) {
        if (issn == null) {
            return "0000-0000";
        }
        if (issn.matches("\\d{4}-\\d{4}")) {
            return issn;
        }
        if (issn.matches("\\d{8}")) {
            return issn.substring(0, 4) + "-" + issn.substring(4);
        }
        return "0000-0000";
    }

    private String getJournalId(Journal journal) {
        if (journal == null || journal.getId() == null || journal.getId().trim().isEmpty()) {
            return "UNKNOWN_JOURNAL";
        }
        return journal.getId();
    }

    private String getJournalTitle(Journal journal) {
        if (journal == null || journal.getTitle() == null || journal.getTitle().trim().isEmpty()) {
            return "Unknown Journal";
        }
        return journal.getTitle();
    }

    @Override
    public double addArticleAndUpdateIF(Article article) {
        // 检查文章是否存在
        boolean articleExists = checkIfArticleExists(article.getId());

        // 插入文章及其相关数据
        if (!articleExists) {
            insertArticleAndRelatedData(article);
        }

        // 计算影响因子A和B
        double A = calculateInfluenceFactorA(article);
        double B = calculateInfluenceFactorB(article);

        // 如果文章存在，删除相关数据
        if (articleExists) {
            deleteArticleRelatedData(article.getId());
        }

        return A / B;
    }

    private boolean checkIfArticleExists(int articleId) {
        String sql = "SELECT count(*) FROM article WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, articleId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking article existence", e);
        }
    }

    private void insertArticleAndRelatedData(Article article) {
        String insertArticleQuery = """
        INSERT INTO article(id, title, pub_model, date_created, date_completed)
        VALUES(?,?,?,?,?);
    """;
        String insertArticleJournalQuery = """
        INSERT INTO article_journal(journal_id, article_id)
        VALUES(?, ?);
    """;
        String insertArticleReferencesQuery = """
        INSERT INTO article_references(article_id, reference_id)
        VALUES(?, ?);
    """;

        try (Connection connection = databaseService.getConnection();
             PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery);
             PreparedStatement journalStmt = connection.prepareStatement(insertArticleJournalQuery);
             PreparedStatement referenceStmt = connection.prepareStatement(insertArticleReferencesQuery)) {

            // 插入文章基本信息
            articleStmt.setInt(1, article.getId());
            articleStmt.setString(2, article.getTitle());
            articleStmt.setString(3, article.getPub_model());
            articleStmt.setDate(4, (Date) article.getCreated());
            articleStmt.setDate(5, (Date) article.getCompleted());
            articleStmt.executeUpdate();

            // 插入文章所属期刊信息
            journalStmt.setString(1, article.getJournal().getId());
            journalStmt.setInt(2, article.getId());
            journalStmt.executeUpdate();

            // 插入文章引用信息
            if (article.getReferences() != null) {
                for (String reference : article.getReferences()) {
                    referenceStmt.setInt(1, article.getId());
                    referenceStmt.setInt(2, Integer.parseInt(reference));
                    referenceStmt.addBatch();
                }
                referenceStmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting article and related data", e);
        }
    }

    private double calculateInfluenceFactorA(Article article) {
        String getAQuery = """
        SELECT count(*)
        FROM article
        JOIN article_references ar ON article.id = ar.article_id
        WHERE reference_id in (
            SELECT article_id
            FROM article a
            JOIN article_journal aj ON a.id = aj.article_id
            WHERE (EXTRACT(year FROM date_completed) = ? OR EXTRACT(year FROM date_completed) = ?)
            AND journal_id = ?
        )
        AND EXTRACT(year FROM date_completed) = ?;
    """;

        try (Connection connection = databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(getAQuery)) {

            Date date = (Date) article.getCreated();
            int year = date.getYear() + 1900 + 1;

            stmt.setInt(1, year - 2);
            stmt.setInt(2, year - 1);
            stmt.setString(3, article.getJournal().getId());
            stmt.setInt(4, year);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating Influence Factor A", e);
        }
    }

    private double calculateInfluenceFactorB(Article article) {
        String getBQuery = """
        SELECT count(*)
        FROM article a
        JOIN article_journal aj ON a.id = aj.article_id
        WHERE (EXTRACT(year FROM date_completed) = ? OR EXTRACT(year FROM date_completed) = ?)
        AND journal_id = ?;
    """;

        try (Connection connection = databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(getBQuery)) {

            Date date = (Date) article.getCreated();
            int year = date.getYear() + 1900 + 1;

            stmt.setInt(1, year - 2);
            stmt.setInt(2, year - 1);
            stmt.setString(3, article.getJournal().getId());

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating Influence Factor B", e);
        }
    }

    private void deleteArticleRelatedData(int articleId) {
        String deleteJournalQuery = "DELETE FROM article_journal WHERE article_id = ?";
        String deleteReferencesQuery = "DELETE FROM article_references WHERE article_id = ?";
        String deleteArticleQuery = "DELETE FROM article WHERE id = ?";

        try (Connection connection = databaseService.getConnection();
             PreparedStatement deleteJournalStmt = connection.prepareStatement(deleteJournalQuery);
             PreparedStatement deleteReferencesStmt = connection.prepareStatement(deleteReferencesQuery);
             PreparedStatement deleteArticleStmt = connection.prepareStatement(deleteArticleQuery)) {

            // 删除相关数据
            deleteJournalStmt.setInt(1, articleId);
            deleteJournalStmt.executeUpdate();

            deleteReferencesStmt.setInt(1, articleId);
            deleteReferencesStmt.executeUpdate();

            deleteArticleStmt.setInt(1, articleId);
            deleteArticleStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting article related data", e);
        }
    }




}
