package com.example.demo10.repository;

import com.example.demo10.entity.Borrow;
import com.example.demo10.entity.Borrow.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserId(Long userId);

    List<Borrow> findByStatus(Status status);

    Optional<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, Status status);

    List<Borrow> findByUserIdAndStatus(Long userId, Status status);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, Status status);
}