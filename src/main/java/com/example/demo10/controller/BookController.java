package com.example.demo10.controller;

import com.example.demo10.dto.BookDTO.*;
import com.example.demo10.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ==================== PUBLIC ENDPOINTS ====================

    /**
     * GET /api/books
     * Returns all books with availability info
     * Access: Public (anyone)
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * GET /api/books/{id}
     * Returns a single book by ID
     * Access: Public
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * GET /api/books/search?title=clean&author=martin&genre=Technology
     * Search and filter books by title, author, genre (all params optional)
     * Access: Public (anyone)
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {
        return ResponseEntity.ok(bookService.searchBooks(title, author, genre));
    }

    /**
     * GET /api/books/available
     * Returns books with at least 1 available copy
     * Access: Public
     */
    @GetMapping("/available")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    // ==================== USER ENDPOINTS ====================

    /**
     * POST /api/books/{id}/review
     * Body: { "rating": 5, "comment": "Great book!" }
     * Access: USER, MANAGER, ADMIN
     */
    @PostMapping("/{id}/review")
    public ResponseEntity<BookResponse> reviewBook(
            @PathVariable Long id,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(bookService.reviewBook(id, request));
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * POST /api/books/create
     * Body: { "title": "...", "author": "...", "isbn": "...", "genre": "...",
     *         "description": "...", "totalCopies": 5 }
     * Access: ADMIN only
     */
    @PostMapping("/create")
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    /**
     * PUT /api/books/update/{id}
     * Access: ADMIN only
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /**
     * DELETE /api/books/delete/{id}
     * Access: ADMIN only
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }
}