# Flaky Test Report

**Name:** Sonejee, Rohit and Tran, Thanh

## Flaky Test 1

**Test name:** `de.seuhd.worldcup.FileBettingServiceTest#test file betting with threads`

**Root cause:**
The test concurrently writes bets to the same file from two different threads which may lead to race conditions during read and write operations. Depending on thread scheduling, updates could overwrite each other, resulting in missing bets.

**Fix:**
We synchronized all file access in FileBettingService using a shared lock object. Both placeBet() and getBets() now execute atomically, preventing race conditions and ensuring that all bets are written and read consistently regardless of thread timing.

## Flaky Test 2

**Test name:** `de.seuhd.worldcup.FileBettingServiceTest#fresh service has no bets`

**Root cause:** Multiple tests shared the same persistent file (SHARED_BET_FILE). Since the test class uses @TestMethodOrder(MethodOrderer.Random::class), the execution order changes between runs. If another test wrote bets into the shared file before this test executed, the file was no longer empty.

**Fix:** We added a cleanup function that deletes the shared file after each test execution using @AfterEach. This ensures that every test starts with a clean and isolated file state, making the tests independent of execution order.

## Flaky Test 3

**Test name:** `de.seuhd.worldcup.WorldCupTest#standings are stable when multiple teams tie on all criteria`

**Root cause:** When teams had identical points, goal difference and goals scored, the standings calculation did not define a final deterministic tie-breaker. The remaining order depended on the iteration order of IdentityHashMap and as a result, teams occasionally appeared in a different order across runs.

**Fix:** We added a final deterministic sorting criterion (team.name) to the standings comparator which eliminates nondeterministic iteration behavior.

## Flaky Test 4

**Test name:** `de.seuhd.worldcup.WorldCupTest#load json from network`

**Root cause:** The test depends on real network calls and a randomly shuffled URL list. This makes the outcome non-deterministic due to network variability and unpredictable URL selection order.

**Fix:** We replaced the real network call with a test double that loads local JSON data, in order to remove nondeterminism by only simulating a real network call. Since we used local data, it does not affect the test but for the sake of removing order dependencies from the code we removed the shuffling of the urls, especially since one of the urls provided does not work. Lastly, we had to increase the timeout value (maybe there is a solution where this is not needed), otherwise the test kept failing.

## Flaky Test 5

**Test name:** `de.seuhd.worldcup.WorldCupTest#evaluate returns zero when no bets are placed`

**Root cause:** BettingService cached evaluation results in cachedResult, but the cache was not cleared when stored bets were removed. Since the cached value persisted between tests, later tests could reuse outdated evaluation results from previous executions, depending on test order.

**Fix:** We reset cachedResult inside the clear() method. This ensures that each test starts with a clean state and that evaluations are always recomputed from the current set of bets instead of reusing outdated cached results.
