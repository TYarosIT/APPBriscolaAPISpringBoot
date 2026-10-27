# projectunisa — Briscola Game API (Spring Boot)

REST API for a two-player **Briscola** card game. The application keeps match state in memory
(players queue, get paired into matches, play cards, and finish games). Controllers are thin
entry points; all business logic lives in `GameService`.

---

## Table of contents

1. [Architecture](#architecture)
2. [Components](#components)
   - [PostunisaApplication](#postunisaapplication)
   - [PostApiController](#postapicontroller)
   - [ApiControllers](#apicontrollers)
   - [GameService](#gameservice)
   - [Models](#models)
3. [Endpoints](#endpoints)
   - [POST /post/…](#post-post)
   - [GET /…](#get-)
4. [Game flow](#game-flow)
5. [Build & run](#build--run)
6. [Tech stack](#tech-stack)

---

## Architecture

```
client (HTTP)
      │
      ▼
┌─────────────────────────────┐
│  Controllers (thin layer)   │  PostApiController  →  POST /post/*
│  @RestController            │  ApiControllers     →  GET /
└──────────────┬──────────────┘
               │ constructor injection
               ▼
┌─────────────────────────────┐
│   GameService (@Service)    │  matchmaking, card play, state
│   (singleton, in-memory)    │
└──────────────┬──────────────┘
               │ owns
               ▼
┌─────────────────────────────┐
│   Models                    │  Partita, Giocatori, JsonPartita
└─────────────────────────────┘
```

The service is a `@Service` singleton, so **all controllers share the same matchmaking pool**:
`POST /post/ready` and `GET /ready` write into the same `pgmap`, `listaplayer` and `listaPartite`.

---

## Components

### PostunisaApplication

`src/main/java/com/turayaros/projectunisa/ProjectunisaApplication.java`

Standard Spring Boot entry point (`@SpringBootApplication`). Starts the embedded servlet container
and component-scans the `com.turayaros.projectunisa` package. No custom configuration is required.

---

### PostApiController

`src/main/java/com/turayaros/projectunisa/Controllers/PostApiController.java`

**Role:** HTTP entry point for mutation / write-heavy operations. It is now a **thin controller**:
it holds no game state and contains no business loops — every method simply delegates to
`GameService`.

**Annotations:** `@RestController` + `@RequestMapping("/post")`.

**Constructor-injected dependency:**

```java
private final GameService gameService;

public PostApiController(GameService gameService) {
    this.gameService = gameService;
}
```

| Method | Mapping | Produces | Delegates to |
|--------|---------|----------|--------------|
| `findPartitaById` | `POST /post/getPartita` | `Partita` | `gameService.findPartitaById(JsonPartita)` |
| `getAnyUpdate` | `POST /post/getAnyUpdate` | `Partita` | `gameService.getAnyUpdate(String)` |
| `postResponseController2` (`putData`) | `POST /post/putData` | `String` | `gameService.putData(Partita)` |
| `udapteData` (`updateData`) | `POST /post/updateData` | `Partita` | `gameService.updateData(JsonPartita)` |
| `rForGame` | `POST /post/ready` | `String` | `gameService.rForGame(String)` |

**What was removed from this class:** static `listaplayer`, `listaPartite`, `pgmap`,
`Stringlistaplayer`, `player1`/`player2` fields, and the private helpers `readyForGame()` and
`carte()`. All of that now lives exclusively in `GameService`.


### ApiControllers

`src/main/java/com/turayaros/projectunisa/Controllers/ApiControllers.java`

**Role:** HTTP entry point for read / polling operations. Also a thin controller delegating to
`GameService`.

**Annotations:** `@RestController` (root mapping, no class-level `@RequestMapping`).

**Constructor-injected dependency:** same `GameService` bean as `PostApiController`.

| Method | Mapping | Produces | Delegates to |
|--------|---------|----------|--------------|
| `getPage` | `GET /w` | `String "welcome"` | — |
| `ready` | `GET /ready` | `String` | `gameService.rForGame(String)` |
| `startGame` | `GET /start` | `String` | `gameService.startGame(String)` |
| `sendCarte` | `GET /carte` | `List<Integer>` | `gameService.sendCarte(String)` |
| `partitaChiusa` | `GET /partitachiusa` | `String` | `gameService.partitaChiusa(String)` |
| `sendTurno` | `GET /turno` | `String` | `gameService.sendTurno(String)` |
| `sendCarta` | `GET /setCarta` | `String` | `gameService.sendCarta(String,int,String)` |
| `getCarta` | `GET /getCarta` | `int` | `gameService.getCarta(String)` |

### GameService

`src/main/java/com/turayaros/projectunisa/Services/GameService.java`

**Role:** Single source of truth for all Briscola game business logic. It is a `@Service` Spring
singleton and owns all mutable in-memory state.

#### State

| Field | Type | Meaning |
|-------|------|---------|
| `listaplayer` | `List<Giocatori>` (synchronized) | Players waiting to be paired |
| `listaPartite` | `List<Partita>` (synchronized) | All created matches |
| `pgmap` | `Map<String,String>` (synchronized) | Player id → match id lookup |
| `waitingPlayers` | `Set<String>` (synchronized) | Dedup set so a player cannot queue twice while waiting |

`logger` uses `java.util.logging.Logger` for server-side tracing.

#### Methods

**Matchmaking**

- **`rForGame(String idp)`** — registers a player and pairs them when the queue size becomes even.
  - `null` → `"wait"`.
  - Already matched → returns the existing `idPartita` from `pgmap` (covers both polling callers).
  - Already queued but not matched → `"wait"` (idempotent re-queue, no duplicates).
  - First player in a pair → queued, returns `"wait"`.
  - Second player → calls `readyForGame()`, creates the `Partita`, puts both players into `pgmap`,
    returns the new `idPartita`.
- **`startGame(String idp)`** — returns the match id for a matched player, or `"wait"` if unknown /
  not yet matched.

**Partita lifecycle (moved from `PostApiController`)**

- **`findPartitaById(JsonPartita)`** — looks up a stored match; falls back to a dummy
  `Partita("id1","id2","id1id2", <40-card deck>)` when nothing matches (legacy behaviour
  preserved).
- **`getAnyUpdate(String idpartita)`** — returns the match or `null`.
- **`putData(Partita p)`** — replaces the stored match with the submitted one (logged), returns
  `"inserted"`.
- **`updateData(JsonPartita)`** — applies a played card + position to `cartaP1`/`posCarta1` (first
  player) or `cartaP2`/`posCarta2` (second player), returns the updated `Partita`, or `null` if not
  found / null input.

**In-game queries & actions**

- **`sendCarte(String idp)`** — the 40-card shuffled deck of the match; empty list if unknown.
- **`partitaChiusa(String idp)`** — `"finita"` / `"ok"` / `"not found"`.
- **`sendTurno(String idp)`** — current `turn` player id, or `"0"`.
- **`sendCarta(String idp, int carta, String player)`** — records the played card for the player,
  flips `turn` to the opponent, returns `"100"` / `"0"`.
- **`getCarta(String idp)`** — last played card value, or `0`.
- **`fine(String idp)`** — sets `partitaFinita = true`, returns `"finita"` / `"not found"`.

**Internal helpers**

- **`readyForGame()`** (`synchronized`) — takes the last two queued players, marks them ready,
  shuffles a 40-card deck (`carte()`), deals `cartaP11..cartaP13` / `cartaP21..cartaP23`, sets
  `turn = first player`, `cartaP1 = cartaP2 = -1`, `partitaFinita = false`, registers both players
  in `pgmap`, stores the match, returns its id.
- **`carte()`** — builds `0..39` and `Collections.shuffle`s it.
- **`findPartitaByIdString(String)`** — private substring lookup over `listaPartite` used by all
  query methods.

#### Thread safety

All shared collections are wrapped with `Collections.synchronized*` and critical sections
(`rForGame`, `readyForGame`, `putData`, `updateData`) use explicit `synchronized` blocks so
concurrent HTTP requests cannot corrupt the match state.


### Models

#### Partita

`src/main/java/com/turayaros/projectunisa/Models/Partita.java`

Represents one Briscola match.

| Field | Type | Meaning |
|-------|------|---------|
| `idFirstPlayer` | `String` | Player 1 id |
| `idSecondPlayer` | `String` | Player 2 id |
| `idPartita` | `String` | Match id (concatenation of both player ids) |
| `carte` | `List<Integer>` | Shuffled 40-card deck (0–39) |
| `turn` | `String` | Id of the player whose turn it is |
| `cartaP1` / `cartaP2` | `int` | Card just played by P1 / P2 (`-1` = none yet) |
| `partitaFinita` | `boolean` | Match finished flag |
| `cartaP11` / `cartaP12` / `cartaP13` | `int` | P1's initial hand |
| `cartaP21` / `cartaP22` / `cartaP23` | `int` | P2's initial hand |
| `posCarta1` / `posCarta2` | `int` | Screen positions of the played cards |

Constructors: no-args, full-field, `(idFirstPlayer, idSecondPlayer, idPartita, turn,
List<Integer> carte)`, and `(idFirstPlayer, idSecondPlayer, idPartita, List<Integer> carte)`.
Full getters/setters and `toString()`.

#### Giocatori

`src/main/java/com/turayaros/projectunisa/Models/Giocatori.java`

Represents a player in the waiting queue.

| Field | Type | Meaning |
|-------|------|---------|
| `id` | `String` | Player id |
| `readyForGame` | `boolean` | Flipped to `true` once paired |
| `info` | `String` | Free-text info |

Constructors: `(String id)` and `(String id, String info)`. Full getters/setters.

#### JsonPartita

`src/main/java/com/turayaros/projectunisa/Models/JsonPartita.java`

DTO used by the client to send a move.

| Field | Type | Meaning |
|-------|------|---------|
| `idPartita` | `String` | Target match id |
| `giocatore` | `String` | Player making the move |
| `Carta` | `int` | Card played |
| `pos` | `int` | Position of the card |

Full getters/setters.


## Endpoints

### POST /post/*

Base path: `/post`. Body-bound JSON unless noted.

| Endpoint | Param / Body | Response | Description |
|----------|--------------|----------|-------------|
| `POST /post/getPartita` | `@RequestBody JsonPartita` | `Partita` | Look up match by id; dummy fallback if unknown. |
| `POST /post/getAnyUpdate` | `@RequestParam String idpartita` | `Partita` | Poll for current match state; `null` if unknown. |
| `POST /post/putData` | `@RequestBody Partita` | `String` | Replace stored match with submitted state → `"inserted"`. |
| `POST /post/updateData` | `@RequestBody JsonPartita` | `Partita` | Record a card + position for P1 or P2; return updated match. |
| `POST /post/ready` | `@RequestParam String idp` | `String` | Join matchmaking queue → `"wait"` or new `idPartita`. |

### GET /*

| Endpoint | Param | Response | Description |
|----------|-------|----------|-------------|
| `GET /w` | — | `"welcome"` | Health / welcome page. |
| `GET /ready` | `@RequestParam String idp` | `String` | Same matchmaking as `POST /post/ready`. |
| `GET /start` | `@RequestParam String idp` | `String` | Return match id if paired, else `"wait"`. |
| `GET /carte` | `@RequestParam String idp` | `List<Integer>` | Match's 40-card deck. |
| `GET /partitachiusa` | `@RequestParam String idp` | `String` | `"finita"`, `"ok"`, or `"not found"`. |
| `GET /turno` | `@RequestParam String idp` | `String` | Current player's turn id, or `"0"`. |
| `GET /setCarta` | `@RequestParam String idp, int carta, String player` | `String` | Play card, flip turn → `"100"` / `"0"`. |
| `GET /getCarta` | `@RequestParam String idp` | `int` | Last played card value, or `0`. |
| `GET /finish` | `@RequestParam String idp` | `String` | Mark match finished → `"finita"` / `"not found"`. |


## Game flow

1. **Join queue** — A player calls `POST /post/ready` (or `GET /ready`) with their id.
   - First player → queued, response `"wait"`.
   - Second player → `readyForGame()` runs: a `Partita` is created with a shuffled 40-card deck,
     each player gets 3 initial cards (`cartaP11-13`, `cartaP21-23`), `turn` is set to player 1,
     both players are mapped to the new `idPartita` in `pgmap`, and the match id is returned.
2. **Start / poll** — Both players call `GET /start` or keep polling `GET /ready` until they receive
   the shared `idPartita` instead of `"wait"`.
3. **Play cards** — On a turn, a player calls `GET /setCarta?idp=…&carta=X&player=…` (or
   `POST /post/updateData`). The service records `cartaP1` or `cartaP2`, updates
   `posCarta1`/`posCarta2`, and flips `turn` to the opponent.
4. **Inspect state** — `GET /carte` (deck), `GET /getCarta` (last played card), `GET /turno`
   (whose turn), `GET /partitachiusa` (finished?).
5. **Finish** — `GET /finish` sets `partitaFinita = true`; subsequent `GET /partitachiusa` returns
   `"finita"`.

## Build & run

Requirements: Java 21+, Maven.

```bash
# compile
mvn compile -DskipTests

# run unit tests
mvn test

# run the application
mvn spring-boot:run
```

The app starts on the default Spring Boot port (`8080`) unless `server.port` is overridden in
`src/main/resources/application.properties`.

Tests live in `src/test/java/com/turayaros/projectunisa/Services/`:

- `GameServiceTest` — matchmaking, deck initialization, turn flipping, safe defaults.
- `GameServicePostLogicTest` — match lookup, `putData`, `updateData` routing for both players.

## Tech stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.4.1-SNAPSHOT (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`) |
| Language | Java 21 |
| Build | Apache Maven |
| Logging | `java.util.logging` |
| Testing | JUnit 5 + AssertJ (via `spring-boot-starter-test`) |
| Database | H2 (runtime scope, included via starter) |

| `fine` | `GET /finish` | `String` | `gameService.fine(String)` |
