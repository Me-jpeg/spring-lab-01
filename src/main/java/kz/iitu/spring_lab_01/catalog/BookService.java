package kz.iitu.spring_lab_01.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository books;
    private final AuthorRepository authors;
    private final TagRepository tags;

    public BookService(BookRepository books, AuthorRepository authors, TagRepository tags) {
        this.books = books;
        this.authors = authors;
        this.tags = tags;
    }

    public List<Book> findAll(String author) {
        List<Book> all = books.findAll();
        if (author == null || author.isBlank()) {
            return all;
        }
        return all.stream()
                .filter(b -> b.authorName() != null && b.authorName().contains(author))
                .toList();
    }

    public Page<Book> findPage(Genre genre, Pageable pageable) {
        if (genre == null) {
            return books.findAll(pageable);
        }
        return books.findByGenre(genre, pageable);
    }

    public Optional<Book> findById(long id) {
        return books.findById(id);
    }

    public Book getOrThrow(long id) {
        return books.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book, String authorName) {
        boolean duplicate = books.findAll().stream()
                .anyMatch(b -> b.getTitle().equals(book.getTitle())
                        && b.authorName() != null && b.authorName().equals(authorName));
        if (duplicate) {
            throw new DuplicateTitleException(book.getTitle(), authorName);
        }
        Author author = resolveAuthor(authorName);
        book.setAuthor(author);
        return books.save(book);
    }

    public Book create(Book book) {
        return create(book, book.authorName());
    }

    public Book update(long id, Book book, String authorName) {
        Book existing = books.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        existing.setTitle(book.getTitle());
        existing.setGenre(book.getGenre());
        existing.setYear(book.getYear());
        existing.setArchived(book.isArchived());
        if (authorName != null) {
            existing.setAuthor(resolveAuthor(authorName));
        }
        return books.save(existing);
    }

    public Book update(long id, Book book) {
        return update(id, book, book.authorName());
    }

    public void delete(long id) {
        if (!books.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        books.deleteById(id);
    }

    public BookStats stats() {
        List<Book> all = books.findAll();
        if (all.isEmpty()) {
            return new BookStats(0, null, null);
        }
        int earliest = all.stream().mapToInt(Book::getYear).min().orElse(0);
        int latest = all.stream().mapToInt(Book::getYear).max().orElse(0);
        return new BookStats(all.size(), earliest, latest);
    }

    public List<Book> search(Genre genre, int year) {
        return books.search(genre, year);
    }

    @Transactional
    public int archiveOlderThan(int year) {
        return books.archiveOlderThan(year);
    }

    public List<Book> findAllWithAuthor() {
        return books.findAllWithAuthor();
    }

    public List<BookBrief> briefByGenre(Genre genre) {
        return books.findBookBriefByGenre(genre);
    }

    private Author resolveAuthor(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("author name is required");
        }
        return authors.findByName(name)
                .orElseGet(() -> authors.save(new Author(name, null)));
    }
}
