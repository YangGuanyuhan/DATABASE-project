# CS307report

**杨官宇涵12313614 （Wednesday 1-2）**（0.5）

task：

1. database design
2. getArticleCitationsByYear API
3. getJournalWithMostArticlesByAuthor
4. writing report



**汤深尧12313624（Wednesday 1-2）**（0.5）

task：

1. other API
2. roles design
3. frontend and the backend
4. Advanced APIs and Other Requirements

## 1. Database Design

### 1.1 ER Diagram

Use website `draw.io` to create the diagram:

[draw.io](https://app.diagrams.net/)

![ERdiagram.drawio](E:\Desktop\数据库原理\project2information\ERdiagram.drawio.png)

### 1.2 Datagrip Visualization

![Datagrip Visualization](E:\Desktop\数据库原理\project2information\datagrip%20visualization.png)

---

## 2. Entity Sets

### 2.1 Entities and Their Attributes

1. **Article**
   - `id`: Unique identifier for the article.
   - `title`: Title of the article.
   - `pub_model`: Publication model (e.g., Print, Electronic, etc.).
   - `date_created`: The date the article was created.
   - `date_completed`: The date the article was completed (nullable).

2. **Journal**
   - `id`: Unique identifier for the journal.
   - `country`: Country where the journal is based.
   - `issn`: ISSN identifier of the journal.
   - `title`: Title of the journal.
   - `volume`: Volume of the journal (nullable).
   - `issue`: Issue of the journal (nullable).

3. **Article_Journal (Relationship between Articles and Journals)**
   - `journal_id`: References the journal ID.
   - `article_id`: References the article ID.
   - Composite primary key ensures each article is uniquely associated with a journal.

4. **Authors**
   - `author_id`: Unique identifier for the author.
   - `fore_name`: First name of the author (optional).
   - `last_name`: Last name of the author (optional).
   - `initials`: Initials of the author's name (optional).
   - `is_collective_name`: Boolean flag indicating if the author is a group.
   - `affiliation`: The author's affiliation (mandatory).

5. **Article_Authors (Relationship between Articles and Authors)**
   - `article_id`: References the article ID.
   - `author_id`: References the author ID.
   - Composite primary key ensures each article-author pair is unique.

6. **article_references (Citation Relationships)**
   - `article_id`: ID of the citing article.
   - `reference_id`: ID of the cited article.
   - Composite primary key ensures uniqueness of the citation relationship.

7. **Publication_Types**
   - `id`: Unique identifier for the publication type.
   - `name`: Name of the publication type.

8. **Article_Publication_Types (Relationship between Articles and Publication Types)**
   - `article_id`: References the article ID.
   - `pub_type_id`: References the publication type ID.
   - Composite primary key ensures each article-publication type pair is unique.

9. **Grant_info**
   - `id`: Unique identifier for the grant.
   - `grant_id`: Grant ID.
   - `acronym`: Acronym of the granting organization.
   - `agency`: Name of the granting agency.
   - `country`: Country of the granting agency (nullable).

10. **Article_Grants (Relationship between Articles and Grants)**
    - `article_id`: References the article ID.
    - `grant_id`: References the grant ID.
    - Composite primary key ensures uniqueness of the article-grant relationship.

11. **Article_Ids**
    - `id`: Auto-incremented primary key.
    - `article_id`: References the article ID.
    - `type`: Type of the identifier (e.g., PubMed, DOI).
    - `identifier`: Specific identifier value.

12. **Keywords**
    - `id`: Unique identifier for the keyword.
    - `keyword`: Keyword text.

13. **Article_Keywords (Relationship between Articles and Keywords)**
    - `article_id`: References the article ID.
    - `keyword_id`: References the keyword ID.
    - Composite primary key ensures each article-keyword pair is unique.

14. **UserEntity**

    - **Purpose:** Represents a user in the system.

    - Attributes:

      - `id`: The unique identifier for the user (`Long`).
      - `username`: The user's login name, which is unique and cannot be null (`String`).
      - `password`: The user's password, which cannot be null (`String`).
      - `email`: The user's email address, which is unique (`String`).
      - `role`: The user's role, which is of type `UserRoleType` (an enum) and defines what permissions the user has in the system (`UserRoleType`).
      - `authorId`: The ID of the author associated with the user (`Long`).
      - `journalId`: The ID of the journal associated with the user (`String`).
      - `fullName`: The user's full name (`String`).
      - `isActive`: Indicates whether the user is active or not (`Boolean`), defaulting to `true`.
      - `lastLogin`: The last time the user logged in, stored as a `ZonedDateTime` (`ZonedDateTime`).
      - `createdAt`: Timestamp of when the user record was created, automatically set by Hibernate (`ZonedDateTime`).
      - `updatedAt`: Timestamp of when the user record was last updated, automatically set by Hibernate (`ZonedDateTime`).

    - Key Methods:

      - `equals()`: Compares two `UserEntity` objects to check for equality based on their attributes.
      - `hashCode()`: Generates a hash code for the object based on its attributes.
      - `toString()`: Provides a string representation of the `UserEntity` object.

    - Annotations:

      - The class is an entity mapped to a `users` table in a database.

      - Uses Hibernate annotations like `@CreationTimestamp` and `@UpdateTimestamp` to manage timestamps automatically.
15. **UserRoleEntity**
    - **Purpose:** Represents a user role in the system.
    - Attributes:
      - `id`: The unique identifier for the role (`Long`).
      - `roleType`: The type of role, represented as an enum (`UserRoleType`), which defines the role in the system (e.g., admin, user).
      - `description`: A description of the role (`String`).
      - `createdAt`: Timestamp of when the role record was created, automatically set by Hibernate (`ZonedDateTime`).
      - `updatedAt`: Timestamp of when the role record was last updated, automatically set by Hibernate (`ZonedDateTime`).
    - Key Methods:
      - `equals()`: Compares two `UserRoleEntity` objects to check for equality based on their attributes.
      - `hashCode()`: Generates a hash code for the object based on its attributes.
      - `toString()`: Provides a string representation of the `UserRoleEntity` object.
    - Annotations:
      - The class is an entity mapped to a `user_roles` table in a database.
      - Uses Hibernate annotations like `@CreationTimestamp` and `@UpdateTimestamp` to manage timestamps automatically.


---

### 2.2 Entity Relationships Description

1. **Relationship Between Articles and Journals**
   - Each article is associated with one journal (many-to-one relationship).
   - Managed via the `Article_Journal` table.

2. **Relationship Between Articles and Authors**
   - An article can have multiple authors, and an author can contribute to multiple articles (many-to-many relationship).
   - Managed via the `Article_Authors` table.

3. **Citation Relationships Between Articles**
   - An article can cite multiple articles, and an article can be cited by multiple articles (many-to-many relationship).
   - Managed via the `article_references` table.

4. **Relationship Between Articles and Publication Types**
   - An article can have multiple publication types (e.g., Print, Electronic).
   - Managed via the `Article_Publication_Types` table.

5. **Relationship Between Articles and Grants**
   - An article can be associated with multiple grants, and a grant can fund multiple articles (many-to-many relationship).
   - Managed via the `Article_Grants` table.

6. **Relationship Between Articles and Keywords**

   - An article can have multiple keywords, and a keyword can be associated with multiple articles (many-to-many relationship).
   - Managed via the `Article_Keywords` table.

7. **Relationship Between the Entities**

- `UserEntity` has a reference to `UserRoleType`, which indicates the role of the user. However, the `UserRoleEntity` class is used to represent the roles more generally in the system (with descriptions and management of role-specific data), while the `UserEntity` associates a user with one of these roles.

---

### 2.3 Triggers for Data Consistency

1. **Trigger for Date Validation in Articles**
   - Ensures `date_completed` is not earlier than `date_created`.

2. **Trigger for Author Data Consistency**
   - Validates author information:
     - Group authors must have only `last_name` populated.
     - Individual authors must have at least `last_name` populated.

3. **Trigger for ISSN Format Validation in Journals**
   - Ensures the ISSN follows the correct format (e.g., `1234-5678`).

---

## 3. Design of the Tables and Columns

Below is a detailed design of the database tables, columns, and their relationships, along with the description of the schema.

### 3.1 Tables

1. **Table: Article**
   - `id` (INT): Primary key, unique identifier for each article.
   - `title` (VARCHAR(1000)): Title of the article, not nullable.
   - `pub_model` (VARCHAR(50)): Publication model with constraints (e.g., Print, Electronic).
   - `date_created` (DATE): The creation date of the article, not nullable.
   - `date_completed` (DATE): The completion date of the article, nullable.
2. **Table: Journal**
   - `id` (VARCHAR(20)): Primary key, unique identifier for each journal.
   - `country` (VARCHAR(200)): Country where the journal is registered.
   - `issn` (VARCHAR(9)): ISSN identifier, with format validation.
   - `title` (VARCHAR(1000)): Title of the journal, not nullable.
   - `volume` (VARCHAR(50)): Volume of the journal, nullable.
   - `issue` (VARCHAR(50)): Issue of the journal, nullable.
3. **Table: Article_Journal**
   - `journal_id` (VARCHAR(20)): Foreign key referencing `Journal.id`.
   - `article_id` (INT): Foreign key referencing `Article.id`.
   - **Primary Key**: Combination of `journal_id` and `article_id`.
4. **Table: Authors**
   - `author_id` (INT): Primary key, unique identifier for each author.
   - `fore_name` (VARCHAR(255)): First name of the author, default is empty.
   - `last_name` (VARCHAR(255)): Last name of the author, default is empty.
   - `initials` (VARCHAR(10)): Initials of the author, default is empty.
   - `is_collective_name` (BOOLEAN): Indicates if the author is a collective.
   - `affiliation` (TEXT): Affiliation of the author.
5. **Table: Article_Authors**
   - `article_id` (INT): Foreign key referencing `Article.id`.
   - `author_id` (INT): Foreign key referencing `Authors.author_id`.
   - **Primary Key**: Combination of `article_id` and `author_id`.
6. **Table: article_references**
   - `article_id` (INT): Foreign key referencing the citing article's `Article.id`.
   - `reference_id` (INT): Foreign key referencing the cited article's `Article.id`.
   - **Primary Key**: Combination of `article_id` and `reference_id`.
7. **Table: Publication_Types**
   - `id` (VARCHAR(20)): Primary key, unique identifier for each publication type.
   - `name` (VARCHAR(200)): Name of the publication type, not nullable.
8. **Table: Article_Publication_Types**
   - `article_id` (INT): Foreign key referencing `Article.id`.
   - `pub_type_id` (VARCHAR(20)): Foreign key referencing `Publication_Types.id`.
   - **Primary Key**: Combination of `article_id` and `pub_type_id`.
9. **Table: Grant_info**
   - `id` (INT): Primary key, unique identifier for each grant.
   - `grant_id` (VARCHAR(50)): Grant ID, nullable.
   - `acronym` (VARCHAR(10)): Acronym of the granting institution, nullable.
   - `agency` (VARCHAR(500)): Name of the granting agency, not nullable.
   - `country` (VARCHAR(100)): Country of the granting agency, nullable.
10. **Table: Article_Grants**
    - `article_id` (INT): Foreign key referencing `Article.id`.
    - `grant_id` (INT): Foreign key referencing `Grant_info.id`.
    - **Primary Key**: Combination of `article_id` and `grant_id`.
11. **Table: Article_Ids**
    - `id` (SERIAL): Auto-incremented primary key.
    - `article_id` (INT): Foreign key referencing `Article.id`.
    - `type` (VARCHAR(50)): Type of identifier (e.g., DOI, PubMed).
    - `identifier` (VARCHAR(255)): Value of the identifier.
12. **Table: Keywords**
    - `id` (SERIAL): Auto-incremented primary key.
    - `keyword` (VARCHAR(100)): Text of the keyword, not nullable.
13. **Table: Article_Keywords**
    - `article_id` (INT): Foreign key referencing `Article.id`.
    - `keyword_id` (INT): Foreign key referencing `Keywords.id`.
    - **Primary Key**: Combination of `article_id` and `keyword_id`.



--------------------------------------------------------------------------

## **4. Database User Creation and Privilege Descriptions**

### **4.1 SQL Script for User Table**

The following SQL script creates the `Users` table with essential fields and role definitions:

```sql
CREATE TABLE Users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('REGULAR_USER', 'AUTHOR', 'JOURNAL') NOT NULL
);
```

### **4.2 Database User Account Creation**

To manage user roles and their respective functionalities, the following database user accounts and privileges are configured.

#### **4.2.1 Regular User**

A read-only user account, intended for accessing user data:

```sql
CREATE USER 'regular_user'@'localhost' IDENTIFIED BY 'password123';
GRANT SELECT ON pubmed.Users TO 'regular_user'@'localhost';
```

#### **4.2.2 Author**

A user account with permissions to read and insert records, used for adding and managing user entries:

```sql
CREATE USER 'author_user'@'localhost' IDENTIFIED BY 'password123';
GRANT SELECT, INSERT ON pubmed.Users TO 'author_user'@'localhost';
```

#### **4.2.3 Journal**

A user account with full data manipulation privileges, allowing reading, inserting, and updating user records:

```sql
CREATE USER 'journal_user'@'localhost' IDENTIFIED BY 'password123';
GRANT SELECT, INSERT, UPDATE ON pubmed.Users TO 'journal_user'@'localhost';
```

### **4.3 Privilege Description**

Below is a detailed breakdown of the privileges assigned to each user role:

#### **4.3.1 `REGULAR_USER`**

- **Access Level:** Read-only.
- Database Operations:
  - `SELECT`: Allows querying records from the `Users` table.

#### **4.3.2 `AUTHOR`**

- **Access Level:** Read and insert records.
- Database Operations:
  - `SELECT`: Allows querying records from the `Users` table.
  - `INSERT`: Allows adding new user records.

#### **4.3.3 `JOURNAL`**

- **Access Level:** Full data manipulation.
- Database Operations:
  - `SELECT`: Allows querying records from the `Users` table.
  - `INSERT`: Allows adding new user records.
  - `UPDATE`: Allows modifying existing user records.

---

## 5. API Specification

### **5.1 API Details: ArticleServiceImpl**

The `ArticleServiceImpl` class provides implementations for managing articles and their related data, including citations, journals, and impact factor calculations. It integrates with the `DatabaseService` to execute SQL operations.

------

#### **5.1.1 Method: `getArticleCitationsByYear`**

**Purpose**:
 Retrieve the number of times an article has been cited in a specific year.

**Implementation Details**:

- **SQL Query**:
  Joins `article_references` and `article` tables to count citations for a given `reference_id` and filter by `date_created` year:

  ```sql
  SELECT COUNT(*)
  FROM article_references ar
  JOIN article a ON ar.article_id = a.id
  WHERE ar.reference_id = ? AND EXTRACT(YEAR FROM a.date_created) = ?
  ```

- **Parameters**:

  - `id` (int): The reference ID of the article.
  - `year` (int): The year to filter citations.

- **Return Value**:
  Number of citations as an integer.

**Error Handling**:
 Returns `0` if an exception occurs.

------

#### **5.1.2 Method: `addArticleAndUpdateIF`**

**Purpose**:
 Add or update an article in the database and calculate the updated journal impact factor.

**Implementation Details**:

- **Steps**:
  1. **Delete Existing Records**: Removes all associated data of the article (e.g., references, keywords, authors) to ensure clean insertion.
  2. **Insert Article**: Adds a new article with an updated `date_created` (set to 2024).
  3. **Insert or Update Journal**: Adds the journal details or updates them if already present.
  4. **Establish Relations**: Inserts a record linking the article to its journal.
  5. **Insert References**: Adds reference data, avoiding duplication using `ON CONFLICT DO NOTHING`.
- **Parameters**:
  - `article` (Article): The article to be added or updated.
- **Return Value**:
  The updated impact factor (double).

**Impact Factor Calculation**:

- Filters journal articles published in the two years prior to the current year.
- Counts their citations in the current year.
- Computes the ratio of citations to articles.

**Error Handling**:

- Rolls back transactions on failure.
- Returns `0.0` if an error occurs.

------

#### **5.1.3 Helper Methods**

- **`formatISSN`**:
  Formats ISSN values to `XXXX-XXXX` format. Returns `"0000-0000"` for invalid or null inputs.

- **`getJournalId`**:
  Returns the journal ID or `"UNKNOWN_JOURNAL"` if null or empty.

- **`getJournalTitle`**:
  Returns the journal title or `"Unknown Journal"` if null or empty.

- **`calculateImpactFactor`**:

  - **SQL Query**:
    Calculates the impact factor based on the number of articles published in the previous two years and citations received in the current year. The formula used is `total_citations / total_articles`.

    ```sql
    SELECT 
        CASE 
            WHEN ac.total_articles = 0 THEN 0.0
            ELSE CAST(COALESCE(c.total_citations, 0) AS FLOAT) / ac.total_articles
        END as impact_factor
    FROM article_count ac
    CROSS JOIN citations_in_year c
    ```

  - **Parameters**:

    - `journalTitle` (String): Title of the journal.
    - `year` (int): The year for which to calculate the impact factor.

  - **Return Value**:
    Impact factor as a double.

------

#### **5.1.4 Error Handling and Transactions**

- **Error Handling**:
  - SQL exceptions are caught, and appropriate rollback actions are performed.
  - Default values are returned in case of failure.
- **Transaction Management**:
  - Deletes and inserts are executed within a single transaction to ensure atomicity.
  - Rollbacks occur on errors to maintain data consistency.

------

### **5.2 Summary**

The `ArticleServiceImpl` class demonstrates robust implementation practices:

1. **SQL Best Practices**:
   - Use of prepared statements to prevent SQL injection.
   - Modular SQL scripts for easier maintenance.
2. **Scalability**:
   - Handles multiple related tables efficiently.
   - Calculates impact factor using dynamic queries.
3. **Reliability**:
   - Comprehensive error handling.
   - Transactions ensure data integrity.

This design ensures the smooth integration of article management with existing database structures. Future improvements could include further optimizing SQL queries or adding caching for frequently accessed data.

------

## 6. API Implementation Report

### **6.1 API Details: AuthorServiceImpl**

The `AuthorServiceImpl` class implements features related to author information and their associated articles. It uses `DatabaseService` for SQL operations and provides methods for retrieving and analyzing data associated with authors.

------

#### **6.1.1 Method: `getArticlesByAuthorSortedByCitations`**

**Purpose**:
 Retrieve all articles authored by a given author, sorted by citation count in descending order, and by article ID in ascending order if citation counts are equal.

**Implementation Details**:

- **SQL Query**:
  Uses common table expressions (CTEs) to:

  1. Identify articles authored by the given individual.
  2. Count distinct citations for each article.
  3. Sort articles by citation count and article ID.

  ```sql
  WITH author_articles AS (
      SELECT aa.article_id
      FROM article_authors aa
      JOIN authors a ON aa.author_id = a.author_id
      WHERE a.fore_name = ? 
      AND a.last_name = ?
      AND (a.is_collective_name = false OR a.is_collective_name IS NULL)
  ),
  article_citations AS (
      SELECT aa.article_id, 
             COUNT(DISTINCT ar2.article_id) AS citation_count
      FROM author_articles aa
      LEFT JOIN article_references ar2 ON ar2.reference_id = aa.article_id
      GROUP BY aa.article_id
  )
  SELECT article_id
  FROM article_citations
  ORDER BY citation_count DESC, article_id ASC
  ```

- **Parameters**:

  - `author` (Author): The author object containing their first and last names.

- **Return Value**:
  An array of article IDs sorted by the specified criteria.

**Error Handling**:
 Returns an empty array if an exception occurs during database interaction.

------

#### **6.1.2 Method: `getJournalWithMostArticlesByAuthor`**

**Purpose**:
 Determine the journal where a specific author has published the most articles.

**Implementation Details**:

- **SQL Query**:

  1. Identifies articles authored by the given author.
  2. Counts the number of articles published in each journal.
  3. Finds the journal with the highest count, breaking ties by alphabetical order.

  ```sql
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
  ```

- **Parameters**:

  - `author` (Author): The author object containing their first and last names.

- **Return Value**:
  The title of the journal with the most articles by the author.

**Error Handling**:
 Returns `null` if no journal is found or an error occurs during execution.

------

#### **6.1.3 Method: `getMinArticlesToLinkAuthors`**

**Purpose**:
 Calculate the minimum number of articles needed to link two authors through shared citations. Implements a breadth-first search (BFS) using recursive SQL.

**Implementation Details**:

- **SQL Query**:
  Uses a recursive common table expression (CTE) to:

  1. Identify the initial set of articles where the first author is listed.
  2. Recursively follow article references to find a path connecting the two authors.
  3. Limits recursion depth to avoid infinite loops.

  ```sql
  WITH RECURSIVE author_connections AS (
      SELECT DISTINCT aa1.article_id as source_article,
             ar.reference_id as target_article,
             1 as depth
      FROM article_authors aa1
      JOIN authors a1 ON aa1.author_id = a1.author_id
      JOIN article_references ar ON aa1.article_id = ar.article_id
      WHERE a1.fore_name = ? AND a1.last_name = ? AND a1.initials = ?
      
      UNION ALL
      
      SELECT ac.source_article,
             ar.reference_id as target_article,
             ac.depth + 1
      FROM author_connections ac
      JOIN article_references ar ON ac.target_article = ar.article_id
      WHERE ac.depth < 5
  )
  SELECT MIN(ac.depth) as min_depth
  FROM author_connections ac
  JOIN article_authors aa ON ac.target_article = aa.article_id
  JOIN authors a ON aa.author_id = a.author_id
  WHERE a.fore_name = ? AND a.last_name = ? AND a.initials = ?
  ```

- **Parameters**:

  - `A` (Author): The starting author.
  - `E` (Author): The target author.

- **Return Value**:
  The minimum number of articles required to link the authors, or `-1` if no link is found.

**Error Handling**:
 Returns `-1` if no connection is found or an error occurs.

------



### ****6.2 API Details: DatabaseServiceImpl**

The `DatabaseServiceImpl` class is responsible for handling various database operations in a Spring-based application. It implements the `DatabaseService` interface and provides methods for data import, truncation, and SQL-based calculations, utilizing JDBC for database communication.

------

#### **6.2.1 Method: `getGroupMembers`**

**Purpose**: Retrieves a predefined list of group member IDs.

**Implementation Details**:

- Returns a fixed list of student IDs as group members.

```java
@Override
public List<Integer> getGroupMembers() {
    return Arrays.asList(12210000, 12210001, 12210002);
}
```

- **Parameters**: None
- **Return Value**: A list of integers representing group member IDs.
- **Error Handling**: No error handling is required as the method simply returns hardcoded values.

------

#### 6.2.2 Method: `importData`

**Purpose**: Inserts dummy data into the `authors` table.

**Implementation Details**:

- Executes an SQL query to insert data into the `authors` table with hardcoded values.

```java
@Override
public void importData(String data_path) {
    String sql = "INSERT INTO authors (fore_name, last_name, initials) VALUES ('test', 'test', 'test')";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
```

- **Parameters**: `data_path` (String) - The path to the data to be imported (though unused here).
- **Return Value**: None
- **Error Handling**: Throws a `RuntimeException` if a `SQLException` occurs.

------

#### **6.2.3 Method: `truncate`**

**Purpose**: Truncates all tables in the `public` schema of a PostgreSQL database.

**Implementation Details**:

- Executes a dynamic SQL script to truncate all tables by first fetching all table names in the schema.

```java
@Override
public void truncate() {
    String sql = "DO $$\n" +
            "DECLARE\n" +
            "    tables CURSOR FOR\n" +
            "        SELECT tablename\n" +
            "        FROM pg_tables\n" +
            "        WHERE schemaname = 'public';\n" +
            "BEGIN\n" +
            "    FOR t IN tables\n" +
            "    LOOP\n" +
            "        EXECUTE 'TRUNCATE TABLE ' || QUOTE_IDENT(t.tablename) || ' CASCADE;';\n" +
            "    END LOOP;\n" +
            "END $$;\n";
    
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
```

- **Parameters**: None
- **Return Value**: None
- **Error Handling**: Throws a `RuntimeException` if a `SQLException` occurs during truncation.

------

#### **6.2.4 Method: `sum`**

**Purpose**: Calculates the sum of two integers using SQL.

**Implementation Details**:

- Executes a SQL query to sum the values of two integers.

```java
@Override
public Integer sum(int a, int b) {
    String sql = "SELECT ?+?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, a);
        stmt.setInt(2, b);

        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
```

- **Parameters**: `a` (int) - The first integer. `b` (int) - The second integer.
- **Return Value**: The sum of the two integers.
- **Error Handling**: Throws a `RuntimeException` if a `SQLException` occurs.

------

#### **6.2.5 Method: `getConnection`**

**Purpose**: Retrieves a database connection.

**Implementation Details**:

- Provides a direct connection to the database using `DataSource`.

```java
@Override
public Connection getConnection() {
    try {
        return dataSource.getConnection();
    } catch (SQLException e) {
        throw new RuntimeException("Failed to get database connection", e);
    }
}
```

- **Parameters**: None
- **Return Value**: A `Connection` object.
- **Error Handling**: Throws a `RuntimeException` if a `SQLException` occurs while obtaining the connection.



------

### **6.3 API Details: GrantServiceImpl**

The `GrantServiceImpl` class manages interactions with grant-related data and articles funded by specific countries.

------

#### **6.3.1 Method: `getCountryFundPapers`**

**Purpose**: Retrieves all article IDs funded by a specific country.

**Implementation Details**:

- Executes an SQL query that joins the `article`, `article_grants`, and `grant_info` tables to find articles associated with grants from a specified country.

```java
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
```

- **Parameters**: `country` (String) - The country for which articles need to be retrieved.
- **Return Value**: An array of integers representing the IDs of funded articles.

------

### 6**.4 API Details: JournalServiceImpl**

The `JournalServiceImpl` class handles functionalities related to journals, including retrieving impact factors and updating journal details.

------

#### **6.4.1 Method: `getImpactFactor`**

**Purpose**: Retrieves the impact factor for a given journal and year, based on article citations.

**Implementation Details**:

- Uses SQL with CTEs to calculate the journal's impact factor by comparing citations and published articles.

```java
@Override
public double getImpactFactor(String journal_title, int year) {
    String sql = """
        WITH journal_articles AS (
            SELECT a.id
            FROM article a
            JOIN article_journal aj ON a.id = aj.article_id
            JOIN journal j ON aj.journal_id = j.id
            WHERE j.title = ?
            AND EXTRACT(YEAR FROM a.date_created) BETWEEN ? AND ?
        ),
        citations AS (
            SELECT COUNT(*) as citation_count
            FROM article_references ar
            JOIN article a ON ar.article_id = a.id
            JOIN journal_articles ja ON ar.reference_id = ja.id
            WHERE EXTRACT(YEAR FROM a.date_created) = ?
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
        stmt.setString(1, journal_title);
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
```

- **Parameters**: `journal_title` (String), `year` (int)
- **Return Value**: Impact factor as a `double`.
- **Error Handling**: Errors are logged, and `0.0` is returned on failure.

------

#### **6.4.2 Method: `updateJournalName`**

**Purpose**: Updates the journal name and its associated articles after a specific year.

**Implementation Details**:

- A transactional operation that ensures data consistency during the update process.

```java
@Override
public boolean updateJournalName(Journal journal, int year, String new_name, String new_id) {
    // SQL operations with transaction management
}
```

- **Parameters**: `journal` (Journal), `year` (int), `new_name` (String), `new_id` (String)
- **Return Value**: `true` if successful, `false` if failed.
- **Error Handling**: Rolls back transaction in case of failure.



------

### 6**.5 API Details: KeywordServiceImpl**

The `KeywordServiceImpl` class allows keyword-based search to retrieve article counts by year.

------

#### **6.5.1 Method: `getArticleCountByKeywordInPastYears`**

**Purpose**: Retrieves the number of articles published with a given keyword each year.

**Implementation Details**:

- Uses recursive CTEs to generate year ranges and calculate article counts for each year.

```java
@Override
public int[] getArticleCountByKeywordInPastYears(String keyword) {
    // SQL with recursive CTEs
}
```

- **Parameters**: `keyword` (String)
- **Return Value**: An array of article counts for each year.
- **Error Handling**: Prints stack trace on error and returns an empty array.



------

## 7. Advanced APIs and Other Requirements

![系统流程及架构-第 1 页.drawio](E:\Desktop\数据库原理\project2information\系统流程及架构-第 1 页.drawio.png)

### Explanation of the UserServiceImpl Class

The `UserServiceImpl` class is an implementation of the `UserService` interface, providing various user-related functionalities for managing users, including login, registration, profile updates, password updates, and user authentication. The class utilizes Spring's dependency injection and transactional management, and interacts with repositories and services like `UserRepository`, `TokenService`, and `JournalService`.

### Key Methods:

1. **login(LoginRequest request)**:

   - This method handles user login. It takes a `LoginRequest` object containing the `username` and `password` and checks if the credentials match a user in the `UserRepository`.
   - If valid, it updates the `lastLogin` timestamp for the user, generates a JWT token using the `TokenService`, and returns a `LoginResponse` with the token and user details.

2. **register(RegisterRequest request)**:

   - Registers a new user. It checks if the username already exists, then creates a new `UserEntity` and saves it to the database.

   - Depending on the user role (

     ```
     AUTHOR
     ```

      or 

     ```
     JOURNAL
     ```

     ), additional validations are performed:

     - For `AUTHOR`: Ensures that an `authorId` is provided.
     - For `JOURNAL`: Ensures that `journalId`, `journalTitle`, and `journalCountry` are provided, and a new `Journal` entity is created and linked to the user.

   - Returns the created `User` object.

3. **updateLastLogin(User user)**:

   - Updates the `lastLogin` field for a user based on the provided user ID.

4. **getUserProfile(String username)**:

   - Retrieves a user's profile details by `username` from the database and returns it as a `User` object.

5. **updateProfile(String username, ProfileUpdateRequest request)**:

   - Allows updating the user's profile (email and full name). It fetches the user by username and updates the corresponding fields.

6. **updatePassword(String username, PasswordUpdateRequest request)**:

   - This method updates a user's password. It checks if the old password provided matches the user's current password, and if valid, updates the password with the new one.

7. **getUsernameFromToken(String token)**:

   - Extracts the username from a JWT token by validating it using the `TokenService`.

### Dependency Injection:

- **TokenService**: This service is used for generating and validating JWT tokens.
- **UserRepository**: This is used for interacting with the `UserEntity` database table.
- **JournalService**: This service is invoked when a `JOURNAL` role user is being registered to create a new `Journal` entity.

### Error Handling:

- The class uses custom exceptions like 

  ```
  InvalidCredentialsException
  ```

   and 

  ```
  EntityNotFoundException
  ```

   to handle cases where:

  - Invalid credentials are provided during login.
  - A user is not found by their `username` or `ID`.
  - Invalid password is provided during password change.

### Role-Based Logic:

- During user registration, if the role is `AUTHOR`, an `authorId` must be provided, and if the role is `JOURNAL`, the system ensures that journal-related information (like `journalId`, `journalTitle`, etc.) is provided and associates the user with a `Journal`.

