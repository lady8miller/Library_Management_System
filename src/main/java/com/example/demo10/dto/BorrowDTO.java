package com.example.demo10.dto;

import com.example.demo10.entity.Borrow;
import java.time.LocalDate;

public class BorrowDTO {

    public static class BorrowResponse {
        public Long id;
        public Long userId;
        public String username;
        public Long bookId;
        public String bookTitle;
        public String status;
        public LocalDate borrowDate;
        public LocalDate dueDate;
        public LocalDate returnDate;

        public BorrowResponse(Borrow borrow) {
            this.id = borrow.getId();
            this.userId = borrow.getUser().getId();
            this.username = borrow.getUser().getUsername();
            this.bookId = borrow.getBook().getId();
            this.bookTitle = borrow.getBook().getTitle();
            this.status = borrow.getStatus().name();
            this.borrowDate = borrow.getBorrowDate();
            this.dueDate = borrow.getDueDate();
            this.returnDate = borrow.getReturnDate();
        }
    }
}