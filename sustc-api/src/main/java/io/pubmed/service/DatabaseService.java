package io.pubmed.service;

import java.sql.Connection;
import java.util.List;

public interface DatabaseService {

    /**
     * Get a database connection from the connection pool
     * @return a database connection
     */
    Connection getConnection();

    /**
     * Acknowledges the authors of this project.
     *
     * @return a list of group members' student-id
     */
    List<Integer> getGroupMembers();

    /**
     * Test your database connection, no need to impl.
     * @param data_path the ndjson file path
     */
    void importData(String data_path);

    /**
     * Truncates all tables in the database.
     * <p>
     * This would only be used in local benchmarking to help you
     * clean the database without dropping it, and won't affect your score.
     */
    void truncate();

    /**
     * Sums up two numbers via Postgres.
     * This method only demonstrates how to access database via JDBC.
     *
     * @param a the first number
     * @param b the second number
     * @return the sum of two numbers
     */
    Integer sum(int a, int b);
}
