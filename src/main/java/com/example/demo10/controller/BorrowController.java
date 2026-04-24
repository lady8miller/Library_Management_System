package com.example.demo10.controller;

import com.example.demo10.dto.BorrowDTO.*;
import com.example.demo10.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // ==================== USER ENDPOINTS ====================

    /**
     * POST /api/borrows/request/{bookId}
     * Creates a PENDING borrow request for the logged-in user
     * Access: USER, MANAGER, ADMIN
     */
    @PostMapping("/request/{bookId}")
    public ResponseEntity<BorrowResponse> requestBorrow(
            @PathVariable Long bookId,
            Authentication auth) {
        return ResponseEntity.ok(borrowService.requestBorrow(bookId, auth.getName()));
    }

    /**
     * POST /api/borrows/return/{borrowId}
     * Returns a borrowed book (sets status to RETURNED)
     * Access: USER, MANAGER, ADMIN (only the owner)
     */
    @PostMapping("/return/{borrowId}")
    public ResponseEntity<BorrowResponse> returnBook(
            @PathVariable Long borrowId,
            Authentication auth) {
        return ResponseEntity.ok(borrowService.returnBook(borrowId, auth.getName()));
    }

    /**
     * GET /api/borrows/my
     * Returns all borrow history for the current logged-in user
     * Access: USER, MANAGER, ADMIN
     */
    @GetMapping("/my")
    public ResponseEntity<List<BorrowResponse>> getMyBorrows(Authentication auth) {
        return ResponseEntity.ok(borrowService.getMyBorrows(auth.getName()));
    }

    // ==================== MANAGER ENDPOINTS ====================

    /**
     * GET /api/borrows/pending
     * Returns all PENDING borrow requests waiting for approval
     * Access: MANAGER, ADMIN
     */
    @GetMapping("/pending")
    public ResponseEntity<List<BorrowResponse>> getPendingBorrows() {
        return ResponseEntity.ok(borrowService.getPendingBorrows());
    }

    /**
     * POST /api/borrows/approve/{borrowId}
     * Approves a PENDING borrow request and decrements available copies
     * Access: MANAGER, ADMIN
     */
    @PostMapping("/approve/{borrowId}")
    public ResponseEntity<BorrowResponse> approveBorrow(@PathVariable Long borrowId) {
        return ResponseEntity.ok(borrowService.approveBorrow(borrowId));
    }

    /**
     * POST /api/borrows/reject/{borrowId}
     * Rejects a PENDING borrow request
     * Access: MANAGER, ADMIN
     */
    @PostMapping("/reject/{borrowId}")
    public ResponseEntity<BorrowResponse> rejectBorrow(@PathVariable Long borrowId) {
        return ResponseEntity.ok(borrowService.rejectBorrow(borrowId));
    }

    /**
     * GET /api/borrows/all
     * Returns all borrow records in the system
     * Access: MANAGER, ADMIN
     */
    @GetMapping("/all")
    public ResponseEntity<List<BorrowResponse>> getAllBorrows() {
        return ResponseEntity.ok(borrowService.getAllBorrows());
    }
}