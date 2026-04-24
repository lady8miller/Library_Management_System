# 📚 Library Management System

A Spring Boot REST API for managing a library — users can browse and borrow books, managers approve requests, and admins control everything.

---

## 👥 Team Member

Aliya Tilebaldieva SCA-24B

---

## Presentation: 



## 🏗️ Project Description

A role-based library backend built with **Spring Boot 3**, **Spring Security + JWT**, and an **H2 in-memory database**.

- **Anyone** can search, filter, and review books
- **Users** can request to borrow and return books
- **Managers** approve or reject borrow requests
- **Admins** manage all users and books

---

## 🔐 Roles & Access

| Role    | Permissions |
|---------|-------------|
| PUBLIC  | Browse & search books, read reviews |
| USER    | + Borrow requests, returns, submit reviews |
| MANAGER | + Approve/reject borrow requests, view all borrows |
| ADMIN   | Full access — manage users and books |

---

## 🗄️ Database Tables

### `users`
| Column   | Type   | Notes              |
|----------|--------|--------------------|
| id       | BIGINT | PK, auto increment |
| username | VARCHAR| Unique             |
| password | VARCHAR| BCrypt encoded     |
| email    | VARCHAR| Unique             |
| role     | VARCHAR| USER/MANAGER/ADMIN |

### `books`
| Column          | Type    | Notes              |
|-----------------|---------|--------------------|
| id              | BIGINT  | PK, auto increment |
| title           | VARCHAR |                    |
| author          | VARCHAR |                    |
| isbn            | VARCHAR | Unique             |
| genre           | VARCHAR |                    |
| description     | VARCHAR |                    |
| total_copies    | INT     |                    |
| available_copies| INT     |                    |
| average_rating  | DOUBLE  | Calculated         |
| review_count    | INT     |                    |

### `borrows`
| Column      | Type    | Notes                              |
|-------------|---------|------------------------------------|
| id          | BIGINT  | PK, auto increment                 |
| user_id     | BIGINT  | FK → users                         |
| book_id     | BIGINT  | FK → books                         |
| status      | VARCHAR | PENDING/APPROVED/REJECTED/RETURNED |
| borrow_date | DATE    |                                    |
| due_date    | DATE    | borrowDate + 14 days               |
| return_date | DATE    | Set on return                      |

---

## 📡 API Endpoints

### 🔓 Auth (Public)
| Method | Endpoint              | Description          |
|--------|-----------------------|----------------------|
| POST   | `/api/auth/register`  | Register a new user  |
| POST   | `/api/auth/login`     | Login, get JWT token |

### 📖 Books (Public)
| Method | Endpoint                    | Description                        |
|--------|-----------------------------|------------------------------------|
| GET    | `/api/books`                | Get all books                      |
| GET    | `/api/books/{id}`           | Get book by ID                     |
| GET    | `/api/books/search`         | Search by title, author, genre     |
| GET    | `/api/books/available`      | Get books with available copies    |

### ⭐ Books (User+)
| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| POST   | `/api/books/{id}/review`    | Submit a rating (1–5)  |

### 📖 Books (Admin only)
| Method | Endpoint                    | Description     |
|--------|-----------------------------|-----------------|
| POST   | `/api/books/create`         | Add a new book  |
| PUT    | `/api/books/update/{id}`    | Update a book   |
| DELETE | `/api/books/delete/{id}`    | Delete a book   |

### 📋 Borrows (User+)
| Method | Endpoint                        | Description                       |
|--------|---------------------------------|-----------------------------------|
| POST   | `/api/borrows/request/{bookId}` | Request to borrow a book          |
| POST   | `/api/borrows/return/{borrowId}`| Return a borrowed book            |
| GET    | `/api/borrows/my`               | View my borrow history            |

### ✅ Borrows (Manager+)
| Method | Endpoint                         | Description                    |
|--------|----------------------------------|--------------------------------|
| GET    | `/api/borrows/pending`           | View all pending requests      |
| POST   | `/api/borrows/approve/{id}`      | Approve a borrow request       |
| POST   | `/api/borrows/reject/{id}`       | Reject a borrow request        |
| GET    | `/api/borrows/all`               | View all borrow records        |

### 👑 Admin
| Method | Endpoint                        | Description             |
|--------|---------------------------------|-------------------------|
| GET    | `/api/admin/users`              | List all users          |
| GET    | `/api/admin/users/{id}`         | Get user by ID          |
| PATCH  | `/api/admin/users/{id}/role`    | Update user role        |
| DELETE | `/api/admin/users/{id}`         | Delete a user           |

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.8+

### 1. Clone / open the project
```bash
cd demo10
```

### 2. Run the application
```bash
./mvnw spring-boot:run
```
The app starts at `http://localhost:8080`

### 3. H2 Console (database browser)
```
URL:      http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:librarydb
Username: sa
Password: (leave empty)
```

### 4. Default test accounts (auto-created on startup)
| Username  | Password    | Role    |
|-----------|-------------|---------|
| admin     | admin123    | ADMIN   |
| manager   | manager123  | MANAGER |
| user1     | user123     | USER    |

---

## 🧪 Postman Testing Guide

### Step 1 — Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "user123"
}
```
Copy the `token` from the response.

### Step 2 — Use Token
Add header to every protected request:
```
Authorization: Bearer <your_token_here>
```

### Step 3 — Example Flow

**As USER:**
1. `GET /api/books` — browse books
2. `GET /api/books/search?genre=Technology` — filter
3. `POST /api/borrows/request/1` — request book ID 1
4. `GET /api/borrows/my` — check your requests

**As MANAGER (login with manager/manager123):**
5. `GET /api/borrows/pending` — see pending requests
6. `POST /api/borrows/approve/1` — approve borrow ID 1

**Back as USER:**
7. `GET /api/borrows/my` — status is now APPROVED
8. `POST /api/borrows/return/1` — return the book

**As ADMIN (login with admin/admin123):**
9. `POST /api/books/create` — add a new book
10. `GET /api/admin/users` — manage all users
11. `PATCH /api/admin/users/3/role` body `{"role":"MANAGER"}` — promote user

---

## 🏛️ Architecture

```
Controller → Service → Repository → H2 Database
     ↑
  JwtFilter (Security)
```

```
com.example.demo10
├── config/           SecurityConfig, DataInitializer
├── controller/       AuthController, BookController, BorrowController, AdminController
├── dto/              AuthDTOs, BookDTO, BorrowDTO
├── entity/           User, Book, Borrow
├── exception/        GlobalExceptionHandler
├── repository/       UserRepository, BookRepository, BorrowRepository
├── security/
│   ├── jwt/          JwtUtil, JwtFilter
│   └── userdetails/  CustomUserDetailsService
└── service/          AuthService, BookService, BorrowService, UserService
```

---

## 🔑 JWT Flow

```
1. POST /api/auth/login  →  Server validates credentials
2. Server generates JWT (expires in 24h)
3. Client sends: Authorization: Bearer <token>
4. JwtFilter extracts username from token
5. Spring Security checks role for the endpoint
6. Access granted or 403 Forbidden
```
