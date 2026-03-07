package com.ctfdensias.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TeamRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    public TeamRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
