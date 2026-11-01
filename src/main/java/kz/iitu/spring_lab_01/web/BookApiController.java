package kz.iitu.spring_lab_01.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.catalog.BookService;
import kz.iitu.spring_lab_01.catalog.BookBrief;
import kz.iitu.spring_lab_01.catalog.BookStats;
import kz.iitu.spring_lab_01.catalog.Genre;
import kz.iitu.spring_lab_01.web.dto.BookMapper;
import kz.iitu.spring_lab_01.web.dto.BookRequest;
import kz.iitu.spring_lab_01.web.dto.BookResponse;
import kz.iitu.spring_lab_01.web.dto.Create;
import kz.iitu.spring_lab_01.web.dto.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Books", description = "The catalogue of books")
@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService service;
    private final BookMapper mapper;

    public BookApiController(BookService service, BookMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<BookResponse> list(
            @RequestParam(required = false) Genre genre,
            @PageableDefault(size = 5, sort = "title") Pageable pageable) {
        return service.findPage(genre, pageable).map(mapper::toResponse);
    }

    @GetMapping("/stats")
    public BookStats stats() {
        return service.stats();
    }

    @GetMapping("/brief")
    public List<BookBrief> brief(@RequestParam Genre genre) {
        return service.briefByGenre(genre);
    }

    @Operation(summary = "Find a book by its identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The book was found"),
        @ApiResponse(responseCode = "404", description = "No book with this id",
                     content = @Content(schema =
                             @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")                                 // 200 or 404
    public BookResponse find(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable long id) {
        return mapper.toResponse(service.getOrThrow(id));
    }

    @PostMapping                                         // 201 + Location
    public ResponseEntity<BookResponse> create(
            @Validated(Create.class) @RequestBody BookRequest request) {
        Book saved = service.create(mapper.toDomain(request), request.author());
        return ResponseEntity
                .created(URI.create("/api/books/" + saved.getId()))
                .body(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")                                 // 200 or 404
    public BookResponse update(@PathVariable long id,
            @Validated(Update.class) @RequestBody BookRequest request) {
        if (request.id() == null || request.id() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Body id must be present and equal to the path id " + id);
        }
        return mapper.toResponse(service.update(id, mapper.toDomain(request), request.author()));
    }

    @DeleteMapping("/{id}")                              // 204 or 404
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
