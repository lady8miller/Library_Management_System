package com.example.demo10.dto;

public class BookDTO {

    public static class BookRequest {
        public String title;
        public String author;
        public String isbn;
        public String genre;
        public String description;
        public int totalCopies;
    }

    public static class BookResponse {
        public Long id;
        public String title;
        public String author;
        public String isbn;
        public String genre;
        public String description;
        public int totalCopies;
        public int availableCopies;
        public double averageRating;
        public int reviewCount;

        public BookResponse(com.example.demo10.entity.Book book) {
            this.id = book.getId();
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.isbn = book.getIsbn();
            this.genre = book.getGenre();
            this.description = book.getDescription();
            this.totalCopies = book.getTotalCopies();
            this.availableCopies = book.getAvailableCopies();
            this.averageRating = book.getAverageRating();
            this.reviewCount = book.getReviewCount();
        }
    }

    public static class ReviewRequest {
        public int rating;   // 1-5
        public String comment;
    }
}