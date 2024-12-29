package io.pubmed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication information class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfo {
    private String username;
    private String password;
}
