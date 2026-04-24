package com.example.demo10.service;

import com.example.demo10.dto.BorrowDTO.*;
import com.example.demo10.entity.*;
import com.example.demo10.entity.Borrow.Status;
import com.example.demo10.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowService(BorrowRepository borrowRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // ---- USER: Request to borrow ----

    @Transactional
    public BorrowResponse requestBorrow(Long bookId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        // Check if already has a pending or approved borrow for this book
        if (borrowRepository.existsByUserIdAndBookIdAndStatus(user.getId(), bookId, Status.PENDING)) {
            throw new RuntimeException("You already have a pending request for this book");
        }
        if (borrowRepository.existsByUserIdAndBookIdAndStatus(user.getId(), bookId, Status.APPROVED)) {
            throw new RuntimeException("You already have this book borrowed");
        }

        Borrow borrow = new Borrow(user, book);
        return new BorrowResponse(borrowRepository.save(borrow));
    }

    // ---- USER: Return a book ----

    @Transactional
    public BorrowResponse returnBook(Long borrowId, String username) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found: " + borrowId));

        if (!borrow.getUser().getUsername().equals(username)) {
            throw new RuntimeException("This borrow does not belong to you");
        }
        if (borrow.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Only approved borrows can be returned");
        }

        borrow.setStatus(Status.RETURNED);
        borrow.setReturnDate(LocalDate.now());

        // Restore available copy
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return new BorrowResponse(borrowRepository.save(borrow));
    }

    // ---- USER: My borrows ----

    public List<BorrowResponse> getMyBorrows(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRepository.findByUserId(user.getId())
                .stream()
                .map(BorrowResponse::new)
                .collect(Collectors.toList());
    }

    // ---- MANAGER: View all pending requests ----

    public List<BorrowResponse> getPendingBorrows() {
        return borrowRepository.findByStatus(Status.PENDING)
                .stream()
                .map(BorrowResponse::new)
                .collect(Collectors.toList());
    }

    // ---- MANAGER: Approve a borrow request ----

    @Transactional
    public BorrowResponse approveBorrow(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrow not found: " + borrowId));

        if (borrow.getStatus() != Status.PENDING) {
            throw new RuntimeException("Only PENDING borrows can be approved");
        }

        Book book = borrow.getBook();
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies of: " + book.getTitle());
        }

        borrow.setStatus(Status.APPROVED);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return new BorrowResponse(borrowRepository.save(borrow));
    }

    // ---- MANAGER: Reject a borrow request ----

    @Transactional
    public BorrowResponse rejectBorrow(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrow not found: " + borrowId));

        if (borrow.getStatus() != Status.PENDING) {
            throw new RuntimeException("Only PENDING borrows can be rejected");
        }

        borrow.setStatus(Status.REJECTED);
        return new BorrowResponse(borrowRepository.save(borrow));
    }

    // ---- MANAGER/ADMIN: All borrows ----

    public List<BorrowResponse> getAllBorrows() {
        return borrowRepository.findAll()
                .stream()
                .map(BorrowResponse::new)
                .collect(Collectors.toList());
    }
}