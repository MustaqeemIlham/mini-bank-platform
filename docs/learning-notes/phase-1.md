Quick check (answer in your head or here):

A user sends POST /api/users with an email like "abc" (no @). Which status code should come back, and whose fault is it?
In your project, account-service asks user-service "does user 7 exist?" Is that a GET or a POST?

answer:
1. 400 eror occur cuz wrong format opf email. 
2. get method 

## What I built
trying to build a mini bank platform using spring boot
## Concepts I can explain
(API, GET vs POST, 4xx vs 5xx, why microservices)
## What broke / how I fixed it
(e.g. did Java 17 vs 21 cause trouble? Did WSL or Docker need a restart?)


# PostgreSQL (already running; after a reboot use: docker start minibank-postgres)
# For reference, this is how I created it:
docker run --name minibank-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=minibank -p 5432:5432 -d postgres:16

# Terminal 1
cd services\user-service; .\mvnw spring-boot:run
# Terminal 2
cd services\account-service; .\mvnw spring-boot:run

http://localhost:8081/swagger-ui.html 
http://localhost:8082/swagger-ui.html


