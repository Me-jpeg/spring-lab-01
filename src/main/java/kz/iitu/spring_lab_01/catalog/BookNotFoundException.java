package kz.iitu.spring_lab_01.catalog;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(long id) {
        super("Book with id " + id + " not found");
    }
}
