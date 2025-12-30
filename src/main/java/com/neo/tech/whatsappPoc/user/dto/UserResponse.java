package com.neo.tech.whatsappPoc.user.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String mobile;
    private String role;
    private boolean active;
}

