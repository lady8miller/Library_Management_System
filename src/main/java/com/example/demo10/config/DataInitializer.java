package com.example.demo10.config;

import com.example.demo10.entity.Book;
import com.example.demo10.entity.User;
import com.example.demo10.repository.BookRepository;
import com.example.demo10.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo,
                                      BookRepository bookRepo,
                                      PasswordEncoder encoder) {
        return args -> {
            // Create default users
            if (!userRepo.existsByUsername("admin")) {
                userRepo.save(new User("admin", encoder.encode("admin123"),
                        "admin@library.com", "ADMIN"));
            }
            if (!userRepo.existsByUsername("manager")) {
                userRepo.save(new User("manager", encoder.encode("manager123"),
                        "manager@library.com", "MANAGER"));
            }
            if (!userRepo.existsByUsername("user1")) {
                userRepo.save(new User("user1", encoder.encode("user123"),
                        "user1@library.com", "USER"));
            }

            // Create sample books
            if (bookRepo.count() == 0) {
                bookRepo.save(new Book("Clean Code", "Robert C. Martin",
                        "978-0132350884", "Technology",
                        "A handbook of agile software craftsmanship", 5));
                bookRepo.save(new Book("The Great Gatsby", "F. Scott Fitzgerald",
                        "978-0743273565", "Fiction",
                        "A novel set in the Jazz Age", 3));
                bookRepo.save(new Book("Design Patterns", "Gang of Four",
                        "978-0201633610", "Technology",
                        "Elements of Reusable Object-Oriented Software", 4));
                bookRepo.save(new Book("1984", "George Orwell",
                        "978-0451524935", "Fiction",
                        "A dystopian social science fiction novel", 6));
                bookRepo.save(new Book("Spring in Action", "Craig Walls",
                        "978-1617294945", "Technology",
                        "The definitive guide to Spring Framework", 3));
            }

            System.out.println("=== Library Data Initialized ===");
            System.out.println("Users: admin/admin123, manager/manager123, user1/user123");
            System.out.println("H2 Console: http://localhost:8080/h2-console");
            System.out.println("JDBC URL: jdbc:h2:mem:librarydb");
        };
    }
}