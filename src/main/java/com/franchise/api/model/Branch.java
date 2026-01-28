package com.franchise.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    
    private String id;
    
    @NotBlank(message = "Branch name is required")
    private String name;
    
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
