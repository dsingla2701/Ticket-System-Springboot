# Database Schema Documentation

## Overview

The Ticket System uses PostgreSQL as the primary database with the following main entities:

- **Users**: System users with different roles
- **Tickets**: Support tickets with lifecycle management
- **Comments**: Threaded comments on tickets
- **Attachments**: File attachments for tickets
- **Ticket Ratings**: User feedback on resolved tickets

## Entity Relationship Diagram

```
Users (1) ----< (M) Tickets
  |                   |
  |                   |
  |                   v
  |               Comments (M)
  |                   |
  |                   |
  v                   v
Attachments (M)   Ticket Ratings (1)
```

## Tables

### users
Stores user information and authentication data.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique user identifier |
| email | VARCHAR(255) | NOT NULL, UNIQUE | User email address |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| first_name | VARCHAR(100) | NOT NULL | User's first name |
| last_name | VARCHAR(100) | NOT NULL | User's last name |
| role | user_role | NOT NULL, DEFAULT 'USER' | User role (USER, SUPPORT_AGENT, ADMIN) |
| is_active | BOOLEAN | NOT NULL, DEFAULT true | Account status |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- `idx_users_email` on email
- `idx_users_role` on role
- `idx_users_active` on is_active

### tickets
Main ticket entity with status and priority management.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique ticket identifier |
| subject | VARCHAR(255) | NOT NULL | Ticket subject/title |
| description | TEXT | NOT NULL | Detailed ticket description |
| status | ticket_status | NOT NULL, DEFAULT 'OPEN' | Current status (OPEN, IN_PROGRESS, RESOLVED, CLOSED) |
| priority | ticket_priority | NOT NULL, DEFAULT 'MEDIUM' | Priority level (LOW, MEDIUM, HIGH, URGENT) |
| created_by_id | UUID | NOT NULL, FK to users(id) | Ticket creator |
| assigned_to_id | UUID | FK to users(id) | Assigned support agent |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Last update timestamp |
| resolved_at | TIMESTAMP WITH TIME ZONE | NULL | Resolution timestamp |
| closed_at | TIMESTAMP WITH TIME ZONE | NULL | Closure timestamp |

**Indexes:**
- `idx_tickets_status` on status
- `idx_tickets_priority` on priority
- `idx_tickets_created_by` on created_by_id
- `idx_tickets_assigned_to` on assigned_to_id
- `idx_tickets_created_at` on created_at
- `idx_tickets_updated_at` on updated_at

### comments
Comments and updates on tickets.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique comment identifier |
| content | TEXT | NOT NULL | Comment content |
| ticket_id | UUID | NOT NULL, FK to tickets(id) | Associated ticket |
| author_id | UUID | NOT NULL, FK to users(id) | Comment author |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- `idx_comments_ticket_id` on ticket_id
- `idx_comments_author_id` on author_id
- `idx_comments_created_at` on created_at

### attachments
File attachments for tickets.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique attachment identifier |
| file_name | VARCHAR(255) | NOT NULL | Stored file name |
| original_file_name | VARCHAR(255) | NOT NULL | Original file name |
| file_size | BIGINT | NOT NULL | File size in bytes |
| mime_type | VARCHAR(100) | NOT NULL | MIME type |
| file_path | VARCHAR(500) | NOT NULL | File storage path |
| ticket_id | UUID | NOT NULL, FK to tickets(id) | Associated ticket |
| uploaded_by_id | UUID | NOT NULL, FK to users(id) | Uploader |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Upload timestamp |

**Indexes:**
- `idx_attachments_ticket_id` on ticket_id
- `idx_attachments_uploaded_by` on uploaded_by_id

### ticket_ratings
User ratings and feedback for resolved tickets.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique rating identifier |
| rating | INTEGER | NOT NULL, CHECK (1-5) | Rating score (1-5 stars) |
| feedback | TEXT | NULL | Optional feedback text |
| ticket_id | UUID | NOT NULL, FK to tickets(id) | Associated ticket |
| rated_by_id | UUID | NOT NULL, FK to users(id) | User who rated |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Rating timestamp |

**Constraints:**
- UNIQUE(ticket_id, rated_by_id) - One rating per user per ticket

**Indexes:**
- `idx_ratings_ticket_id` on ticket_id
- `idx_ratings_rated_by` on rated_by_id

## Enums

### user_role
- `USER`: Regular system user
- `SUPPORT_AGENT`: Support team member
- `ADMIN`: System administrator

### ticket_status
- `OPEN`: Newly created ticket
- `IN_PROGRESS`: Ticket being worked on
- `RESOLVED`: Issue resolved, awaiting closure
- `CLOSED`: Ticket closed and completed

### ticket_priority
- `LOW`: Low priority issue
- `MEDIUM`: Standard priority (default)
- `HIGH`: High priority issue
- `URGENT`: Critical issue requiring immediate attention

## Triggers

### update_updated_at_column()
Automatically updates the `updated_at` timestamp when records are modified.

Applied to:
- users table
- tickets table
- comments table

## Migration Strategy

Database migrations are managed using Flyway with the following naming convention:
- `V{version}__{description}.sql`
- Example: `V1__Create_initial_schema.sql`

## Security Considerations

1. **Password Storage**: Passwords are hashed using BCrypt
2. **UUID Primary Keys**: UUIDs prevent enumeration attacks
3. **Foreign Key Constraints**: Ensure data integrity
4. **Indexes**: Optimized for common query patterns
5. **Timestamps**: All entities track creation and modification times
