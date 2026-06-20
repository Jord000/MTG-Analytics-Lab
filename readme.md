# MTG analytics lab

An application to submit Magic The Gathering Commander game results to then provide analysis and data observability about the games. MTG is used as a fun example but the application could be configured for all sorts of data analysis. The intention of the portfolio piece is to show proficiency in the following software

- Java, Spring Boot
- Opensearch / NoSql Data storage
- Docker
- Kubernetes

# Useful commands

build the jar
`./mvnw clean install`

run springboot
`./mvnw spring-boot:run`

make a get call via powershell
`Invoke-RestMethod -Uri "http://localhost:8080/"`
`curl http://localhost:8080`

run local opensearch instance
`docker compose up -d`

make a post to the game entry index via the api

```
curl.exe -i -X POST http://localhost:8080/game_entry -H "Content-Type: application/json" -d "{\"player\":\"Alice\",\"commander\":\"Atraxa, Praetors' Voice\",\"colorIdentity\":\"Green, White, Blue, Black\",\"result\":\"Win\",\"numberOfTurnsPlayed\":10}"
```
