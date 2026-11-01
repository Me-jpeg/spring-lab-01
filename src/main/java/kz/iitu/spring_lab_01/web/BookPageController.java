package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.catalog.BookService;
import kz.iitu.spring_lab_01.catalog.Genre;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;

    public BookPageController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String author,
            Model model) {
        model.addAttribute("books", service.findAll(author));
        model.addAttribute("author", author);
        model.addAttribute("title", "Catalogue");
        return "books/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("genres", Genre.values());
        return "books/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Model model) {
        model.addAttribute("book", service.getOrThrow(id));
        return "books/detail";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") BookForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("genres", Genre.values());
            return "books/form";
        }
        Book book = new Book(form.getTitle(), form.getGenre(), form.getYear());
        Book saved = service.create(book, form.getAuthor());
        redirect.addFlashAttribute("message",
                "Book «" + saved.getTitle() + "» has been added");
        return "redirect:/books";
    }
}
