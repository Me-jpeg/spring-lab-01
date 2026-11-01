package kz.iitu.spring_lab_01.web.dto;

import java.util.List;
import kz.iitu.spring_lab_01.catalog.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toDomain(BookRequest request) {
        Book book = new Book(request.title(), request.genre(), request.year());
        if (request.id() != null) {
            book.setId(request.id());
        }
        return book;
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.authorName(),
                book.getYear(), book.getGenre(), book.getCreatedAt());
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
