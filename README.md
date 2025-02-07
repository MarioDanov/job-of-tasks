# job-of-tasks

This is a Spring Boot application that processes jobs by sorting tasks based on their dependencies. It also can generate a bash script with the commands. The application leverages Spring's validation framework for robust input validation, provides custom error handling with `@ControllerAdvice`, and includes integrated logging for easier debugging.

## Features

- **Task Dependency Resolution:**  
  Uses Kahn's algorithm to topologically sort tasks based on their declared dependencies.

- **Bash Script Generation:**  
  Generate a bash script that executes tasks in the correct order.

- **Input Validation:**  
  Validates incoming JSON requests using JSR-380 (`@Valid` and validation annotations) ensuring robust data integrity.

- **Custom Error Handling:**  
  Centralized exception handling via `@ControllerAdvice` provides clear and consistent error responses.

- **Logging:**  
  SLF4J logging is integrated to trace key operations and errors (e.g., cycle detection and dependency issues).

- **Testing:**  
  Includes unit tests (for service logic) and integration tests (for controller endpoints) using JUnit 5 and Spring Boot Test.

Test classes are located under `src/test/java/com/jobs/jobprocessor/` and follow a similar package structure.

## Prerequisites

- **Java 11** (or later)
- **Maven** or **Gradle** for building the project
- [Spring Boot](https://spring.io/projects/spring-boot)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/MarioDanov/job-of-tasks.git
cd jobprocessor
