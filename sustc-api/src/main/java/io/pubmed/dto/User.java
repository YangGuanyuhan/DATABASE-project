package io.pubmed.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data

@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User {

    private Long id;

    private String username;

    private String password;

    private UserRole role;

}
