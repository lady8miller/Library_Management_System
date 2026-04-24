package com.example.demo10.service;

import com.example.demo10.dto.BorrowDTO.*;
import com.example.demo10.entity.*;
import com.example.demo10.entity.Borrow.Status;
import com.example.demo10.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock private BorrowRepository borrowRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private BorrowService borrowService;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testUser = new User("user1", "encoded_pass", "user1@mail.com", "USER");
        testBook = new Book("Clean Code", "Robert C. Martin",
                "978-0132350884", "Technology", "A book", 5);
    }

    @Test
    @DisplayName("requestBorrow creates PENDING borrow when book is available")
    void requestBorrow_success() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowRepository.existsByUserIdAndBookIdAndStatus(any(), any(), eq(Status.PENDING)))
                .thenReturn(false);
        when(borrowRepository.existsByUserIdAndBookIdAndStatus(any(), any(), eq(Status.APPROVED)))
                .thenReturn(false);
        when(borrowRepository.save(any(Borrow.class))).thenAnswer(i -> i.getArguments()[0]);

        BorrowResponse response = borrowService.requestBorrow(1L, "user1");

        assertEquals("PENDING", response.status);
        assertEquals("user1", response.username);
    }

    @Test
    @DisplayName("requestBorrow throws when user already has PENDING request")
    void requestBorrow_alreadyPending_throws() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowRepository.existsByUserIdAndBookIdAndStatus(any(), any(), eq(Status.PENDING)))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> borrowService.requestBorrow(1L, "user1"));

        assertTrue(ex.getMessage().contains("pending request"));
    }

    @Test
    @DisplayName("approveBorrow sets APPROVED and decrements available copies")
    void approveBorrow_success() {
        Borrow borrow = new Borrow(testUser, testBook);
        borrow.setStatus(Status.PENDING);
        testBook.setAvailableCopies(5);

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(borrowRepository.save(any(Borrow.class))).thenAnswer(i -> i.getArguments()[0]);

        BorrowResponse response = borrowService.approveBorrow(1L);

        assertEquals("APPROVED", response.status);
        assertEquals(4, testBook.getAvailableCopies());
    }

    @Test
    @DisplayName("approveBorrow throws when no copies available")
    void approveBorrow_noAvailableCopies_throws() {
        testBook.setAvailableCopies(0);
        Borrow borrow = new Borrow(testUser, testBook);
        borrow.setStatus(Status.PENDING);

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> borrowService.approveBorrow(1L));

        assertTrue(ex.getMessage().contains("No available copies"));
    }

    @Test
    @DisplayName("returnBook sets RETURNED and restores available copy")
    void returnBook_success() {
        Borrow borrow = new Borrow(testUser, testBook);
        borrow.setStatus(Status.APPROVED);
        testBook.setAvailableCopies(2);

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(borrowRepository.save(any(Borrow.class))).thenAnswer(i -> i.getArguments()[0]);

        BorrowResponse response = borrowService.returnBook(1L, "user1");

        assertEquals("RETURNED", response.status);
        assertEquals(3, testBook.getAvailableCopies());
    }

    @Test
    @DisplayName("returnBook throws when borrow does not belong to user")
    void returnBook_wrongUser_throws() {
        Borrow borrow = new Borrow(testUser, testBook);
        borrow.setStatus(Status.APPROVED);

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> borrowService.returnBook(1L, "other_user"));

        assertTrue(ex.getMessage().contains("does not belong to you"));
    }
}