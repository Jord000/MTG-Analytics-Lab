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
