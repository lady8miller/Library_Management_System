package com.example.demo10.service;

import com.example.demo10.dto.BookDTO.*;
import com.example.demo10.entity.Book;
import com.example.demo10.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ---- Admin: CRUD ----

    public BookResponse createBook(BookRequest request) {
        Book book = new Book(
                request.title,
                request.author,
                request.isbn,
                request.genre,
                request.description,
                request.totalCopies
        );
        return new BookResponse(bookRepository.save(book));
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findById(id);
        book.setTitle(request.title);
        book.setAuthor(request.author);
        book.setIsbn(request.isbn);
        book.setGenre(request.genre);
        book.setDescription(request.description);
        book.setTotalCopies(request.totalCopies);
        return new BookResponse(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }

    // ---- Public: Search & View ----

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    public BookResponse getBookById(Long id) {
        return new BookResponse(findById(id));
    }

    public List<BookResponse> searchBooks(String title, String author, String genre) {
        return bookRepository.searchBooks(title, author, genre)
                .stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0)
                .stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    // ---- User: Review ----

    public BookResponse reviewBook(Long id, ReviewRequest request) {
        if (request.rating < 1 || request.rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        Book book = findById(id);

        // Recalculate average rating
        double newTotal = (book.getAverageRating() * book.getReviewCount()) + request.rating;
        book.setReviewCount(book.getReviewCount() + 1);
        book.setAverageRating(newTotal / book.getReviewCount());

        return new BookResponse(bookRepository.save(book));
    }

    // ---- Internal helpers ----

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }
}