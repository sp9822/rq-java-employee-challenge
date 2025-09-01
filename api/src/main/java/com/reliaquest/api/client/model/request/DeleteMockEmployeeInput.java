package com.reliaquest.api.client.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteMockEmployeeInput {

    @NotBlank
    private String name;
}
