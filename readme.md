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
`curl "http://localhost:8080"`

run local opensearch instance
`docker compose up -d`

Useful GET's and POST's using Powershell

```
curl.exe --% -i -X POST http://localhost:8080/game_entry -H "Content-Type: application/json" -d "{\"player\":\"Alice\",\"commander\":\"Atraxa, Praetors' Voice\",\"colorIdentity\":\"WBUG\",\"win\":true,\"numberOfTurnsPlayed\":10}"
```

```
curl.exe -i "http://localhost:8080/game_entry?player=Alice&commander=Atraxa%2C%20Praetors'%20Voice&colorIdentity=G&colorIdentity=WUBG"
```

```
curl.exe -i "http://localhost:8080/game_entry?player=Jordan&colorIdentity=G"
```
