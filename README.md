# NotesApplication

A simple Spring Boot application for managing notes with MongoDB.  
This project allows creating, updating, deleting, and filtering notes by tags. Each note contains a title, text, creation time, and a set of tags. It also calculates word statistics for note content.

---

## Features

- Create, read, update, and delete notes (CRUD operations)  
- Filter notes by tags (`BUSINESS`, `PERSONAL`, `IMPORTANT`)  
- Pagination and sorting of notes  
- Word statistics for each note  
- REST API endpoints for integration with frontend or other services  
- Unit tests using JUnit 5 and Mockito

---

## Technologies

- Java 21  
- Spring Boot 3.3  
- Spring Data MongoDB  
- MongoDB  
- Lombok  
- Gradle for build  
- JUnit 5 + Mockito for testing  
- Docker (optional, for containerized deployment)

---

## Prerequisites

- Java 21 JDK  
- MongoDB (local or Docker)  
- Gradle (optional, IntelliJ IDEA includes it)  
- Docker (optional, for running the app as a container)

---

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/RustamTech/NotesApplication.git
cd NotesApplication
```

### 2. Configure MongoDB

Make sure MongoDB is running locally on the default port (`27017`).

The application uses the database `notesdb` (defined in `application.yml`):

```yaml
spring:
  data:
    mongodb:
      database: notesdb
```
You can also run MongoDB using Docker:
docker run -d -p 27017:27017 --name mongodb mongo:latest

### 3. Build the project

```
./gradlew build

```

This will generate a JAR file in build/libs/NotesApplication-1.0-SNAPSHOT.jar.

### 4. Run the application
Option 1: Using Gradle
./gradlew bootRun

Option 2: Using JAR
java -jar build/libs/NotesApplication-1.0-SNAPSHOT.jar`

Option 3: Using Docker
Build Docker image:
docker build -t notesapp .

Run container:
docker run -p 8080:8080 notesapp

| Method | URL                                                                   | Request Body                                                                                      | Description                               |
| ------ | --------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- | ----------------------------------------- |
| POST   | `/api/notes`                                                          | `{ "title": "Sample Note", "text": "This is a note example", "tags": ["BUSINESS", "IMPORTANT"] }` | Create a new note                         |
| PUT    | `/api/notes/{id}`                                                     | `{ "title": "Updated Note", "text": "Updated text", "tags": ["PERSONAL"] }`                       | Update an existing note                   |
| DELETE | `/api/notes/{id}`                                                     | -                                                                                                 | Delete a note by ID                       |
| GET    | `/api/notes/{id}`                                                     | -                                                                                                 | Get a note by ID                          |
| GET    | `/api/notes/summaries?page=0&size=10&sortBy=createdTime&sortDir=desc` | -                                                                                                 | Get paginated note summaries              |
| GET    | `/api/notes/filter?tags=BUSINESS,IMPORTANT&page=0&size=10`            | -                                                                                                 | Get notes filtered by tags                |
| GET    | `/api/notes?page=0&size=10&sortBy=createdTime&sortDir=desc`           | -                                                                                                 | Get all notes with pagination and sorting |


Running Tests

The project includes unit tests using JUnit 5 and Mockito.
Run tests using Gradle:

```
./gradlew test
```
