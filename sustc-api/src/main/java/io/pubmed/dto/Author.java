package io.pubmed.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The article_id information class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author implements Serializable {
    private String fore_name;

    private String last_name;

    private String initials;

    private String collective_name;

    public String getForeName() {
        return fore_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getInitials() {
        return initials;
    }

    public String getCollectiveName() {
        return collective_name;
    }
}
