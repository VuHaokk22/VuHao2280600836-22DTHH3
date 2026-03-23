package com.example.bookmanager.controller;

import com.example.bookmanager.model.Book;
import com.example.bookmanager.model.CartItem;
import com.example.bookmanager.repository.BookRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping("/books")
public class BookController {


    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private BookRepository repository;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String category,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "asc") String sort) {

        Sort sortOrder = sort.equals("desc")
                ? Sort.by("price").descending()
                : Sort.by("price").ascending();

        Pageable pageable = PageRequest.of(page, 5, sortOrder);
        Page<Book> bookPage;

        boolean hasKeyword = !keyword.isEmpty();
        boolean hasCategory = !category.isEmpty();

        if (hasKeyword && hasCategory) {
            bookPage = repository.findByTitleContainingIgnoreCaseAndCategory(keyword, category, pageable);
        } else if (hasKeyword) {
            bookPage = repository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if (hasCategory) {
            bookPage = repository.findByCategory(category, pageable);
        } else {
            bookPage = repository.findAll(pageable);
        }

        List<String> categories = repository.findAll().stream()
                .map(Book::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct().sorted().toList();

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", categories);
        return "list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("book", new Book());
        return "form";
    }

    @PostMapping
    public String save(@ModelAttribute Book book,
                       @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Files.copy(imageFile.getInputStream(), Paths.get(UPLOAD_DIR + filename), StandardCopyOption.REPLACE_EXISTING);
            book.setImageUrl("/uploads/" + filename);
        } else if (book.getId() != null) {
            repository.findById(book.getId()).ifPresent(existing -> book.setImageUrl(existing.getImageUrl()));
        }
        repository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Optional<Book> opt = repository.findById(id);
        if (opt.isPresent()) {
            model.addAttribute("book", opt.get());
            return "form";
        }
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/books";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        Optional<Book> opt = repository.findById(id);
        if (opt.isEmpty()) return "redirect:/books";

        Book book = opt.get();
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new LinkedHashMap<>();

        if (cart.containsKey(id)) {
            cart.get(id).setQuantity(cart.get(id).getQuantity() + quantity);
        } else {
            cart.put(id, new CartItem(id, book.getTitle(), book.getPrice(), quantity));
        }
        session.setAttribute("cart", cart);
        return "redirect:/books";
    }
}
