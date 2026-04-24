package com.example.demo10.service;

import com.example.demo10.dto.BookDTO.*;
import com.example.demo10.entity.Book;
import com.example.demo10.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Book("Clean Code", "Robert C. Martin",
                "978-0132350884", "Technology",
                "A handbook of agile software craftsmanship", 5);
    }

    @Test
    @DisplayName("getAllBooks returns list of BookResponse")
    void getAllBooks_returnsAll() {
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        List<BookResponse> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).title);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getBookById throws RuntimeException when book not found")
    void getBookById_notFound_throws() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookService.getBookById(999L));

        assertTrue(ex.getMessage().contains("Book not found"));
    }

    @Test
    @DisplayName("createBook saves and returns BookResponse")
    void createBook_success() {
        BookRequest req = new BookRequest();
        req.title = "New Book";
        req.author = "Author";
        req.isbn = "123-456";
        req.genre = "Fiction";
        req.description = "A new book";
        req.totalCopies = 3;

        Book savedBook = new Book(req.title, req.author, req.isbn,
                req.genre, req.description, req.totalCopies);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponse response = bookService.createBook(req);

        assertEquals("New Book", response.title);
        assertEquals(3, response.totalCopies);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("reviewBook updates average rating correctly")
    void reviewBook_updatesRating() {
        // Book has 0 reviews
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        ReviewRequest req = new ReviewRequest();
        req.rating = 4;

        BookResponse response = bookService.reviewBook(1L, req);

        assertEquals(4.0, response.averageRating);
        assertEquals(1, response.reviewCount);
    }

    @Test
    @DisplayName("reviewBook throws when rating is out of range")
    void reviewBook_invalidRating_throws() {
        ReviewRequest req = new ReviewRequest();
        req.rating = 6; // invalid

        assertThrows(RuntimeException.class, () -> bookService.reviewBook(1L, req));
    }

    @Test
    @DisplayName("deleteBook calls repository delete")
    void deleteBook_success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        doNothing().when(bookRepository).delete(sampleBook);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(sampleBook);
    }
}