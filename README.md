# WorldCup (IASE, SS 26) - Assignment 2

A console-based Kotlin application that loads the 2026 FIFA World Cup group-stage
data from a JSON file and lets the user view standings, list matches, place bets on
individual matches, and check how many predictions were correct.

This assignment has two parts:

1. **Fix the flaky tests** — five tests in this project are intentionally flaky.
   Find them, explain why they fail intermittently, and fix the root cause.
2. **Implement the stubs** — three methods in `BettingService` and their tests in
   `BettingServiceTest` are left intentionally empty. Fill in both the production
   code and the test bodies.

## Prerequisites

* JDK 25. The Gradle build requests this via toolchains, so any JDK that Gradle can
  locate or auto-provision will do; there is no need to set `JAVA_HOME` manually.
* No separate Gradle install is needed; the included Gradle wrapper (`./gradlew`)
  takes care of it.
* Recommended IDE: IntelliJ IDEA. VS Code with Kotlin extensions also works.

## Project layout

```
src/main/kotlin/de/seuhd/worldcup/
    Main.kt                     Entry point and menu loop
    Console.kt                  Small input helpers (readInt, waitForEnter, ...)
    Data.kt                     @Serializable data classes mirroring the JSON schema
    JsonLoader.kt               Loads the bundled JSON resource from the classpath
    StandingsService.kt         Builds a sorted standings table for a group
    BettingService.kt           In-memory bet store and evaluation (contains stubs)
    FileBettingService.kt       File-backed bet store (used by the flaky tests)
src/main/resources/
    world_cup_2026_full_data.json   Tournament data (groups + knockouts)
src/test/kotlin/de/seuhd/worldcup/
    WorldCupTest.kt             Logic tests for standings and betting
    FileBettingServiceTest.kt   Tests against FileBettingService (contains flaky tests)
    BettingServiceTest.kt       Tests for the stub methods (bodies left empty for you)
```

## Build, run, test

From the project root:

```sh
./gradlew build       # compile + test
./gradlew test        # tests only
./gradlew run         # start the interactive console
./gradlew repeatTests # run the test suite 20 times to surface flaky tests
```

In IntelliJ: *File > Open* the project root, let Gradle import finish, then run
`Main.kt` from the gutter, or use the *Gradle* tool window to invoke
`application > run`.

## Part 1: Fix the flaky tests

There are **five intentionally flaky tests** in this project. `./gradlew test` may
pass on a given run, fail on another, or fail with a different combination of tests
each time. Use `./gradlew repeatTests` (20 runs) to get a reliable picture.

For each flaky test you should:

1. Reproduce the failure and convince yourself you understand the *root cause*.
2. Fix the production code, the test, or both, whichever is actually wrong. A fix
   that pins test execution order or wraps the assertion in a retry does not count.
3. Verify with `./gradlew repeatTests` that the suite is now stable across all 20
   runs.

A short writeup of what was wrong and why your fix addresses the root cause is part
of the deliverable.

## Part 2: Implement the stubs

`BettingService.kt` contains three methods whose bodies are `TODO(...)`. Implement
them according to their KDoc comments:

| Method | Description |
|---|---|
| `evaluateBonus(matches)` | Returns the total bonus score: 3 points for an exact score match, 1 point for a correct outcome only, 0 otherwise. |
| `removeBet(matchId)` | Removes the bet for the given match. Does nothing if no bet exists. |
| `changeBet(bet)` | Updates an existing bet. Throws `IllegalArgumentException` if no bet exists for that match. |

`BettingServiceTest.kt` contains matching empty test methods. Fill in each test body
so it actually verifies the described behaviour. Every test must contain at least one
`assert*` call — a `TODO(...)` body is not a passing test.

## Menu

```
1) Show Standings        - prints a single group's table (or all 12 if you press Enter)
2) Show Matches          - lists matches of a group, with score if the match was played
3) Place Bets            - iterates through one group's matches, asking for 1 / 2 / 0
4) Show Betting Score    - compares stored bets against the actual results
5) Exit
```

The bet codes follow the assignment text: `1` = home win, `2` = away win, `0` = draw.

## Standings rules

* Win = 3 points, Draw = 1 point, Loss = 0 points.
* Tie-break: better goal difference, then more goals scored.
* Matches with `null` scores are unplayed and do not contribute to the table.

## Data file

`src/main/resources/world_cup_2026_full_data.json` ships with the project and is
loaded from the classpath, so the program runs from any working directory (IDE,
`./gradlew run`, or a packaged jar). The schema:

* `tournament`: tournament name.
* `groups[]`: each has `name`, `teams[]`, and `matches[]`.
  Each match carries `matchId`, `round`, `date`, `homeTeam`/`awayTeam` (team IDs),
  `homeScore`/`awayScore` (`null` until played), and `ground` (venue name).
* `knockouts[]`: parsed for completeness but out of scope for this assignment.
