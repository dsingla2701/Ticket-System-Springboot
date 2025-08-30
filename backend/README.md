# Ticket System Backend

Spring Boot REST API for the Ticket Management System.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **User Management**: CRUD operations for users with different roles
- **Ticket Management**: Complete ticket lifecycle management
- **Comment System**: Threaded comments on tickets
- **File Attachments**: Secure file upload and download
- **Email Notifications**: Automated notifications for ticket events
- **API Documentation**: Swagger/OpenAPI documentation

## Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway** for database migrations
- **Maven** for dependency management

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 13+

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE ticketing_system;
```

2. Update database credentials in `src/main/resources/application.yml`

### Running the Application

1. Install dependencies:
```bash
mvn clean install
```

2. Run database migrations:
```bash
mvn flyway:migrate
```

3. Start the application:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

### API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

## Project Structure

```
src/
├── main/
│   ├── java/com/ticketsystem/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security configuration
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── db/migration/   # Flyway migrations
│       └── application.yml # Configuration
└── test/                   # Test classes
```

## Environment Variables

- `JWT_SECRET`: Secret key for JWT token signing
- `MAIL_USERNAME`: SMTP username for email notifications
- `MAIL_PASSWORD`: SMTP password for email notifications
- `FILE_UPLOAD_DIR`: Directory for file uploads

## Testing

Run tests with:
```bash
mvn test
```

## Building for Production

```bash
mvn clean package
java -jar target/ticket-system-backend-1.0.0.jar
```
