package kz.iitu.spring_lab_01.catalog;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class DataSeeder implements CommandLineRunner {

    private final BookRepository books;
    private final AuthorRepository authors;
    private final TagRepository tags;
    private final BookService service;

    public DataSeeder(BookRepository books, AuthorRepository authors,
            TagRepository tags, BookService service) {
        this.books = books;
        this.authors = authors;
        this.tags = tags;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (books.count() > 0) {
            return;
        }
        Author bloch = new Author("Joshua Bloch", "USA");
        Author martin = new Author("Robert Martin", "USA");
        Author walls = new Author("Craig Walls", "USA");
        authors.saveAll(List.of(bloch, martin, walls));

        Tag java = new Tag("java");
        Tag spring = new Tag("spring");
        Tag clean = new Tag("clean-code");
        Tag patterns = new Tag("patterns");
        tags.saveAll(List.of(java, spring, clean, patterns));

        create("Effective Java", Genre.SCIENCE, 2018, bloch, List.of(java, patterns));
        create("Java Puzzlers", Genre.SCIENCE, 2005, bloch, List.of(java));
        create("Java Concurrency in Practice", Genre.SCIENCE, 2006, bloch, List.of(java));
        create("Clean Code", Genre.SCIENCE, 2008, martin, List.of(clean));
        create("Clean Architecture", Genre.SCIENCE, 2017, martin, List.of(clean, patterns));
        create("Spring in Action", Genre.SCIENCE, 2022, walls, List.of(spring, java));
        create("The Hobbit", Genre.FANTASY, 1937, new Author("J.R.R. Tolkien", "UK"), List.of(patterns));
        create("Sapiens", Genre.HISTORY, 2011, new Author("Yuval Harari", "Israel"), List.of());
    }

    private void create(String title, Genre genre, int year, Author author, List<Tag> tagList) {
        Author managed = authors.findByName(author.getName())
                .orElseGet(() -> authors.save(author));
        Book book = new Book(title, genre, year);
        book.setAuthor(managed);
        book.getTags().addAll(tagList);
        books.save(book);
    }
}
