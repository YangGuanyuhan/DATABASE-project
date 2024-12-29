package io.pubmed.service.impl;

import io.pubmed.dto.Author;
import io.pubmed.service.AuthorService;
import io.pubmed.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private DatabaseService databaseService;

    @Override

    public int[] getArticlesByAuthorSortedByCitations(Author author) {
        String sql1 = """
                WITH authors_article AS (
                                                                                SELECT aa.article_id
                                                                                FROM authors a
                                                                                         JOIN article_authors aa ON a.author_id = aa.author_id
                                                                                WHERE a.last_name = ?
                                                                                  AND a.is_collective_name = false
                                                                            ),
                                                                             article_citations as(
                                                                                SELECT
                                                                                    aa2.article_id,
                                                                                    COALESCE(COUNT(ar.reference_id), 0) AS reference_count
                                                                                FROM
                                                                                    authors_article aa2
                                                                                        LEFT JOIN
                                                                                    article_references ar ON ar.reference_id = aa2.article_id
                                                                                GROUP BY
                                                                                    aa2.article_id
                                                                                ORDER BY
                                                                                    reference_count DESC)
                                                                            SELECT article_id
                                                                            FROM article_citations;
            """;
        String sql2 = """
                WITH authors_article AS (
                    SELECT aa.article_id
                    FROM authors a
                             JOIN article_authors aa ON a.author_id = aa.author_id
                    WHERE a.last_name is null
                      AND a.is_collective_name = true
                      AND a.fore_name is null
                      AND a.initials=?
                ),
                 article_citations as(
                SELECT
                    aa2.article_id,
                    COALESCE(COUNT(ar.reference_id), 0) AS reference_count
                FROM
                    authors_article aa2
                        LEFT JOIN
                    article_references ar ON ar.reference_id = aa2.article_id
                GROUP BY
                    aa2.article_id
                ORDER BY
                    reference_count DESC)
                SELECT article_id
                FROM article_citations;
            """;

        String sql3 = """
                WITH authors_article AS (
                                             SELECT aa.article_id
                                             FROM authors a
                                                      JOIN article_authors aa ON a.author_id = aa.author_id
                                             WHERE a.last_name = ?
                                               AND a.is_collective_name = false
                                               AND a.fore_name = ?
                                         ),
                                          article_citations as(
                                             SELECT
                                                 aa2.article_id,
                                                 COALESCE(COUNT(ar.reference_id), 0) AS reference_count
                                             FROM
                                                 authors_article aa2
                                                     LEFT JOIN
                                                 article_references ar ON ar.reference_id = aa2.article_id
                                             GROUP BY
                                                 aa2.article_id
                                             ORDER BY
                                                 reference_count DESC)
                                         SELECT article_id
                                         FROM article_citations;
            """;
        ArrayList<Integer> result=new ArrayList<>();
        try (Connection con = databaseService.getConnection();
             PreparedStatement statement1 = con.prepareStatement(sql1);
             PreparedStatement statement2 = con.prepareStatement(sql2);
             PreparedStatement statement3 = con.prepareStatement(sql3);
        ) {if (author.getCollective_name() == null && author.getLast_name() != null && author.getFore_name() == null ) {
            statement2.setString(1, author.getLast_name());
            ResultSet resultSet2 = statement2.executeQuery();

            while (resultSet2.next()) {
                result.add(resultSet2.getInt("article_id"));

            }
        }
            if (author.getCollective_name() != null && author.getLast_name() == null && author.getFore_name() == null ) {
                statement1.setString(1, author.getCollective_name());
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    result.add(resultSet1.getInt("article_id"));
                }
            }

            if (author.getCollective_name() == null && author.getLast_name() != null && author.getFore_name() != null  ) {
                statement3.setString(1, author.getLast_name());
                statement3.setString(2, author.getFore_name());
                ResultSet resultSet3 = statement3.executeQuery();
                while (resultSet3.next()) {
                    result.add(resultSet3.getInt("article_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result.stream().mapToInt(i -> i).toArray();
    }
@Override
public String getJournalWithMostArticlesByAuthor(Author author) {
    String sql = """
            WITH author_articles AS (
                SELECT DISTINCT aa.article_id
                FROM article_authors aa
                JOIN authors a ON aa.author_id = a.author_id
                WHERE a.fore_name = ?
                AND a.last_name = ?
                AND (a.is_collective_name = false OR a.is_collective_name IS NULL)
            ),
            journal_counts AS (
                SELECT j.title, COUNT(DISTINCT aa.article_id) as article_count
                FROM author_articles aa
                JOIN article_journal aj ON aa.article_id = aj.article_id
                JOIN journal j ON aj.journal_id = j.id
                GROUP BY j.title
            ),
            max_count AS (
                SELECT MAX(article_count) as max_count
                FROM journal_counts
            )
            SELECT jc.title
            FROM journal_counts jc, max_count mc
            WHERE jc.article_count = mc.max_count
            ORDER BY jc.title ASC
            LIMIT 1
        """;

        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, author.getForeName());
            stmt.setString(2, author.getLastName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getMinArticlesToLinkAuthors(Author A, Author E) {
        // This is a bonus task - implementing BFS to find shortest path
        String sql = """
            WITH RECURSIVE author_connections AS (
                -- Initial set: articles where author A is an author
                SELECT DISTINCT aa1.article_id as source_article,
                       ar.reference_id as target_article,
                       1 as depth
                FROM article_authors aa1
                JOIN authors a1 ON aa1.author_id = a1.author_id
                JOIN article_references ar ON aa1.article_id = ar.article_id
                WHERE a1.fore_name = ? AND a1.last_name = ? AND a1.initials = ?
                
                UNION ALL
                
                -- Recursive part: follow references
                SELECT ac.source_article,
                       ar.reference_id as target_article,
                       ac.depth + 1
                FROM author_connections ac
                JOIN article_references ar ON ac.target_article = ar.article_id
                WHERE ac.depth < 5  -- Limit depth to prevent infinite recursion
            )
            SELECT MIN(ac.depth) as min_depth
            FROM author_connections ac
            JOIN article_authors aa ON ac.target_article = aa.article_id
            JOIN authors a ON aa.author_id = a.author_id
            WHERE a.fore_name = ? AND a.last_name = ? AND a.initials = ?
        """;

        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set parameters for author A
            stmt.setString(1, A.getForeName());
            stmt.setString(2, A.getLastName());
            stmt.setString(3, A.getInitials());
            // Set parameters for author E
            stmt.setString(4, E.getForeName());
            stmt.setString(5, E.getLastName());
            stmt.setString(6, E.getInitials());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int depth = rs.getInt("min_depth");
                return depth > 0 ? depth : -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
//    public int findMinimumArticlesToLinkAuthors(Author A, Author B) {
//        // 1. 获取作者参与的文章集合
//        Set<Integer> articlesA = getArticlesByAuthor(A.);  // 获取作者A的所有文章
//        Set<Integer> articlesB = getArticlesByAuthor(authorB);  // 获取作者B的所有文章
//
//        // 2. 如果两个作者都有相同的文章，直接返回0
//        if (!Collections.disjoint(articlesA, articlesB)) {
//            return 0;
//        }
//
//        // 3. 建立文章引用关系图
//        Map<Integer, Set<Integer>> referencesGraph = buildReferencesGraph();
//
//        // 4. 使用BFS找到最短路径
//        Queue<Integer> queue = new LinkedList<>();
//        Set<Integer> visited = new HashSet<>();
//
//        // 将所有作者A的文章加入队列
//        for (int article : articlesA) {
//            queue.add(article);
//            visited.add(article);
//        }
//
//        int level = 0;
//        while (!queue.isEmpty()) {
//            int size = queue.size();
//            for (int i = 0; i < size; i++) {
//                int currentArticle = queue.poll();
//
//                // 获取当前文章引用的所有文章
//                Set<Integer> referencedArticles = referencesGraph.get(currentArticle);
//                if (referencedArticles != null) {
//                    for (int nextArticle : referencedArticles) {
//                        // 如果目标作者的文章已经被访问过，返回当前层数
//                        if (articlesB.contains(nextArticle)) {
//                            return level + 1;
//                        }
//
//                        // 如果当前文章没有被访问过，加入队列
//                        if (!visited.contains(nextArticle)) {
//                            visited.add(nextArticle);
//                            queue.add(nextArticle);
//                        }
//                    }
//                }
//            }
//            level++;
//        }
//
//        // 如果遍历完成后没有找到目标作者的文章，返回-1
//        return -1;
//    }
//
//    // 获取某个作者的所有文章
//    private Set<Integer> getArticlesByAuthor(int authorId) {
//        Set<Integer> articles = new HashSet<>();
//        String sql = """
//                select article_id
//                from article_authors
//                where author_id=?;
//        """;
//        try (Connection conn = databaseService.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            // Set parameters for author A
//            stmt.setString(1, A.getForeName());
//            stmt.setString(2, A.getLastName());
//            stmt.setString(3, A.getInitials());
//            // Set parameters for author E
//            stmt.setString(4, E.getForeName());
//            stmt.setString(5, E.getLastName());
//            stmt.setString(6, E.getInitials());
//
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                int depth = rs.getInt("min_depth");
//                return depth > 0 ? depth : -1;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return articles;
//    }
//
//    // 构建文章引用关系图
//    private Map<Integer, Set<Integer>> buildReferencesGraph() {
//        Map<Integer, Set<Integer>> graph = new HashMap<>();
//        // 数据库查询或其他方式获取文章之间的引用关系，构建图
//        return graph;
//    }

}
