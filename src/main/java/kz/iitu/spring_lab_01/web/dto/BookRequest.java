package kz.iitu.spring_lab_01.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import kz.iitu.spring_lab_01.catalog.Genre;

public record BookRequest(
        @Null(message = "{book.id.forbidden}", groups = Create.class)
        @NotNull(message = "{book.id.required}", groups = Update.class)
        Long id,

        @NotBlank(message = "{book.title.required}")
        @Size(max = 120, message = "{book.title.size}")
        String title,

        @NotBlank(message = "{book.author.required}")
        String author,

        @Min(value = 1450, message = "{book.year.range}")
        @Max(value = 2100, message = "{book.year.range}")
        int year,

        @NotNull(message = "{book.genre.required}")
        Genre genre) {
}
