### Microservices Fundamentals project

This project aims to:
- Introduce microservices architecture, including commonly used tools, technologies, and patterns such as API Gateway, Service Discovery, and Circuit Breaker.
- Develop an understanding of the applicability and use cases of microservices architecture.
- Reinforce learned concepts through practical tasks and exercises.
- Teach best practices and approaches for production-ready microservices.

### Run locally

Set those values:
in resource-service/src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres

in resource-service/src/main/java/com/epam/resource/client/SongServiceClient.java:
String songServiceUrl = "http://localhost:8081/songs";

in: song-service/src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres

### Run in Docker

Set those values:
in resource-service/src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://db:5432/postgres

in resource-service/src/main/java/com/epam/resource/client/SongServiceClient.java:
String songServiceUrl = "http://song-service:8081/songs";

in: song-service/src/main/resources/application.properties:
spring.datasource.url=jdbc:postgresql://db:5432/postgres




