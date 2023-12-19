package com.ll.medium.domain.post.post.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WriteForm {
    @Size(max = 30)
    @NotBlank
    private String title;

    @Size(max = 5000)
    @NotBlank
    private String content;

    private Boolean isPublished;
}
