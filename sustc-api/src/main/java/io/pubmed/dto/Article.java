package io.pubmed.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The article information class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {
    /**
     * Article's main id, pmid.
     */
    private int id;

    /**
     * The article's authors.
     */
    private Author[] authors;

    /**
     * The article's title.
     */
    private String title;

    /**
     * The article's keywords.
     */
    private String[] keywords;

    /**
     * Journal of this article.
     */
    private Journal journal;

    /**
     * List of article's references to other articles.
     */
    private String[] references;

    private ArticleID[] article_ids;

    private PublicationType[] publication_types;

    /**
     * Grants awarded to this article.
     */
    private Grant[] grants;

    /**
     * Creation date of the article
     */
    private Date created;

    /**
     * Completion date of the article
     */
    private Date completed;

    /**
     * Publication model of this article.
     */
    private String pub_model;

    public Date getDateCreated() {
        return created;
    }

    public Date getDateCompleted() {
        return completed;
    }

    public String getPubModel() {
        return pub_model;
    }
}
