# Ticketing System

A full-stack ticketing system for IT support and customer service management.

## Tech Stack used here:

### Backend
- **Java** with **Spring Boot**
- **PostgreSQL** database
- **Spring Security** for authentication
- **JWT** for token-based authentication

### Frontend
- **Next.js** (React-based framework)
- **TypeScript** for type safety
- **Tailwind CSS** for styling

## Project Structure

```
ticket-system/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── README.md
├── frontend/                # Next.js application
│   ├── src/
│   │   ├── app/
│   │   ├── components/
│   │   ├── lib/
│   │   └── types/
│   ├── package.json
│   └── README.md
├── database/                # Database scripts and migrations
│   ├── migrations/
│   └── seeds/
└── docs/                    # Documentation
    ├── api/
    └── deployment/
```

## Features

### Must-Have Features ✅
- [x] Authentication & Authorization (Role-based: User, Admin, Support Agent)
- [x] User Dashboard (Create tickets, view status, add comments)
- [x] Ticket Management (Full lifecycle: Open → In Progress → Resolved → Closed)
- [x] Admin Panel (User management, ticket oversight)
- [x] Access Control (Role-based permissions)

### Good-to-Have Features 🌟
- [ ] Email Notifications
- [ ] Search & Filter
- [ ] Ticket Prioritization
- [ ] File Attachments
- [ ] Rate Ticket Resolution

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 13+
- Maven 3.6+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Database Setup
```bash
# Create database
createdb ticketing_system

# Run migrations (from backend directory)
mvn flyway:migrate
```

## API Documentation

The API documentation will be available at `http://localhost:8080/swagger-ui.html` when the backend is running.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
