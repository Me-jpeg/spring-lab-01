package kz.iitu.spring_lab_01.web.dto;

import java.time.Instant;
import kz.iitu.spring_lab_01.catalog.Genre;

public record BookResponse(
        Long id,
        String title,
        String author,
        int year,
        Genre genre,
        Instant createdAt) {
}
