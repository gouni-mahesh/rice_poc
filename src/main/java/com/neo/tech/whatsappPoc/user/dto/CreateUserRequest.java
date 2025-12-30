package com.neo.tech.whatsappPoc.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String mobile;

    @NotNull
    private String role;
}

