# Social Network — Microservices with Java & Spring Boot

This project is a **simple but practical example of a microservices-based social network**, built using **Java Spring Boot**. It is perfect for beginners who want to explore service communication, distributed systems, and real backend development patterns.

---

## Technologies Used

| Layer                | Technology                                                                 |
|----------------------|----------------------------------------------------------------------------|
| Backend Framework    | Java 17, Spring Boot, Spring Cloud                                         |
| Service Discovery    | Eureka (Client + Server)                                                   |
| API Gateway          | Spring Cloud Gateway                                                       |
| Async Communication  | Apache Kafka                                                               |
| Caching / Sessions   | Redis                                                                      |
| Database             | PostgreSQL                                                                 |
| File Storage         | AWS S3                                                                     |
| Containerization     | Docker Compose                                                             |
| Hosting Ready        | AWS EC2 or any VPS supporting Docker                                       |


---

## Microservices Overview

| Folder                             | Description                                                                |
|------------------------------------|----------------------------------------------------------------------------|
| `api-gateway`                      | Central gateway that routes all incoming requests to respective services.  |
| `authentication-client`            | Handles user registration, login, and JWT-based authentication.            |
| `eureka-server-application`        | Main Eureka server for service registration and discovery.                 |
| `eureka-client-messenger`          | Manages private messaging between users.                                   |
| `eureka-file-store`                | Uploads and retrieves media files using AWS S3.                            |
| `eureka-friends-followers-service` | Follows/unfollows and friend request logic.                                |
| `news-feed-service`                | Builds personalized news feeds from followed users.                        |
| `post-service`                     | Handles posts, comments, and post metadata.                                |
| `rating-service`                   | Calculates user popularity based on interactions.                          |
| `search-history`                   | Stores and retrieves user search queries.                                  |

All services communicate through **REST APIs**, **Kafka**, and are registered via **Eureka**.

## How to Run the Project
---
```bash


### 1. Configure Environment Variables

- Copy `.env example` → `.env`
- Copy `.config/common.properties.example` → `.config/common.properties`
- Fill out the required fields (DB access, Kafka ports, AWS keys, etc.)
```
> 💡 Need help setting up MySQL, Redis, Kafka, generating JWT, or configuring AWS S3?  
> Check out the [Dev Setup Guide](./dev-setup-guide.md) for step-by-step instructions.
```

### 2. Build the Services


chmod +x build.sh
./build.sh

This will compile and prepare all Docker images for each microservice.

### 3. Start Everything

docker-compose up

All services will launch automatically and be accessible via API Gateway.
```
---

Who Is This For?

This project is great for:

- Beginners who want to understand **how microservices work** in real projects.
- Junior Java Developers learning **Spring Boot and backend architecture**.
- Anyone preparing for **Java interviews** or building a **portfolio**.
- Curious minds who want to explore **Kafka, Redis, AWS S3, Eureka**, and more.

It’s a hands-on way to see how real systems are built and how services talk to each other.

---

📚 **Want to dive deeper into the architecture and APIs?**  
Check out the [📘 Full Wiki Documentation](https://github.com/2k0st01/Social-network/wiki)


___

### Author

**Stanislav Kosto**  
Back-End Developer | Java & Spring Boot Expert | Microservices & Cloud Enthusiast | Kafka, REST, Docker, AWS | SQL & Redis | CI/CD & Monitoring Focused

[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin&style=for-the-badge)](https://www.linkedin.com/in/stanislav-kosto/)

___

Feel free to fork the repo, experiment, and build your own scalable backend with it!

