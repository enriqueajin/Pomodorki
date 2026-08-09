# AGENTS.md

## Role

You are an experienced Android engineer working in this repository. Prefer Kotlin, Jetpack Compose, Material 3, Coroutines/Flow, Hilt, and Unidirectional Data Flow (UDF). Match existing Pomidorki patterns before introducing new ones. Keep changes small, verify with the commands below, and do not invent architecture the app does not yet use.

## Project

Pomidorki is a Pomodoro timer + task-management Android app. The countdown runs in a **foreground service** so the timer survives backgrounding; UI binds to that service and observes its state.

| Module | Path | Role |
|--------|------|------|
| `:app` | `app/` | Features, domain/data packages, DI, FGS, navigation |
| `:shared:design-system` | `shared/design-system/` | Reusable Compose UI (theme + components). Do not depend on `:app`. |

Package root: `com.enriqueajin.pomidorki`.

## Commands

```bash
./gradlew ktlintCheck
./gradlew ktlintFormat
./gradlew :app:testDebugUnitTest
./gradlew :app:testDebugUnitTest --tests "com.enriqueajin.pomidorki.presentation.home.TimerScreenViewModelTest"
./gradlew assemble
```

CI (`.github/workflows/ci.yml`) runs: `ktlintCheck` → `:app:testDebugUnitTest` → `assemble`. Instrumented tests under `androidTest` are **not** in CI unless that changes.

Before finishing non-trivial work: format/check with ktlint and run the relevant unit tests (or the full unit suite).

## Architecture map

Layers live as **packages inside `:app`** (not separate Gradle modules):

```
app/.../pomidorki/
  presentation/     # Compose UI, ViewModels, navigation, feature folders
  domain/           # models, repository interfaces (use_cases/ currently empty)
  data/             # repository impls, DataStore, countdown, CountdownService
  di/               # Hilt modules
  utils/            # shared helpers, prefs keys/defaults
```

**Presentation** is organized by feature: `home/` (timer), `tasks/`, `stats/`, `pomodorosettings/`, `taskdetail/`, `permissionhandling/`, `navigation/`, `ui/theme/`.

**Data persistence today:** DataStore Preferences (`UserSettingsRepository` / `UserSettingsRepositoryImpl`). No Room. Tasks are still dummy/in-memory. Firebase is on the classpath and in DI but not used by feature code yet.

**Timer flow (do not bypass):**

```
UI Event → ViewModel → Effect / Intent
  → ServiceHelper / CountdownService (FGS)
  → CountDownPomodoro (+ DataStore session restore)
  → CountdownService.serviceData (StateFlow)
  → Activity bind → ViewModel → uiState → Compose
```

`TimerScreenViewModel` is activity-scoped so timer state survives bottom-nav switches.

## UI and state conventions

- For any Jetpack Compose work (UI, state, modifiers, navigation, theming, performance, design-system components), **always** load and follow the `compose-expert` skill before implementing or reviewing.
- Prefer `*ScreenRoot` (collect state, wire VM, side effects) + `*Screen` (stateless UI).
- Prefer UDF: immutable state down, events up.
- For interactive flows with one-shot side effects, prefer a **Contract** with `State` / `Event` / `Effect` (canonical: `presentation/home/TimerScreenContract.kt`). Settings/Tasks currently use separate `*State` + `*Event` files — do not invent a third style; when touching a screen, align toward the Contract pattern if the change is already substantial.
- Expose UI state as `StateFlow` (often `stateIn(..., WhileSubscribed(5_000))` or `MutableStateFlow` + `asStateFlow`).
- Collect with `collectAsStateWithLifecycle`.
- One-shot effects: `Channel` + `receiveAsFlow` (timer pattern).
- DI: Hilt (`@HiltViewModel`, `@AndroidEntryPoint`, modules under `di/`). Inject dispatchers rather than hard-coding where tests need control.
- Shared reusable visuals belong in `:shared:design-system` when they are productized components, not one-off screen chrome.

## Testing conventions

### Stack (today)

- Prefer JVM unit tests in `app/src/test` (ViewModels, mappers, utils). Mirror production packages.
- Libraries in use: **JUnit4**, **MockK**, **Turbine** (`app.cash.turbine`), **kotlinx-coroutines-test** (`runTest`, virtual time), **Robolectric** (medium data-layer tests that need Android APIs on the JVM).
- `testOptions.unitTests.isIncludeAndroidResources = true` is required for Robolectric host tests in `:app`.
- Prefer **fakes** for ViewModels (`fake/FakeUserSettingsRepository.kt`); **MockK** for Android collaborators that are awkward to fake (e.g. `CountdownService`).
- For real repository / DataStore wiring, use Robolectric + a temp/in-memory DataStore (canonical: `data/repository/UserSettingsRepositoryImplTest.kt`), not the fake.
- Use `MainDispatcherRule` / `Dispatchers.setMain` for Main-dependent code.
- Compose UI tests live under `app/src/androidTest` (component-level today; expand later). They are **not** in CI yet.

### Naming: GIVEN / WHEN / THEN

Encode scenario in the **test function name** (backticks), not in body comments.

- **GIVEN** — optional. Include only when a precondition, input, or scenario matters (state, prefs, prior events).
- **WHEN** — the action under test (event, function, call).
- **THEN** — observable outcome (state field updated, effect emitted, return value, side effect on fake).

Examples (match existing suite):

- `` `GIVEN idle timer WHEN OnTabClicked new index THEN selectedTimer updated and TriggerIntent emitted` ``
- `` `WHEN OnRestartIconClick THEN dialog opens and TimerToBeConfirmed emitted` `` (no GIVEN — default setup is enough)
- `` `GIVEN empty preferences THEN state with default values` `` (WHEN implied by construction/collection)

**Do not** write `// GIVEN`, `// WHEN`, `// THEN` in the test body. Separate phases with a **blank line** only.

### Stubs and instances

Prefer the cheapest shared setup that stays hermetic:

1. **Global (class-level)** — `@Before` / fields for repository fake, ViewModel, common mocks (see `TimerScreenViewModelTest`).
2. **Global + re-stub per case** — keep the shared instance; change inputs for that test (`serviceDataFlow.value = ...`, `fake.putString(...)`, `every { ... } returns ...`).
3. **Per-test instances** — last resort when a case needs a truly fresh graph or incompatible initial state (some `UserSettingsViewModelTest` cases).

Do not create a new mock/fake in every test by default if a shared setup works.

### Skills (use when relevant)

**Unit / JVM (primary now)**

| Skill | When |
|-------|------|
| `applying-testing-strategies` | Structure, determinism, hermetic tests, GWT naming |
| `choosing-what-to-test` | What to cover vs skip |
| `picking-test-doubles` | Fake vs mock vs stub |
| `mocking-with-mockk` | MockK APIs (`every`, `verify`, coroutines) |
| `testing-flows-with-turbine` | Asserting Flow / StateFlow / Channel emissions |
| `testing-coroutines-with-runtest` | `runTest`, test dispatchers, virtual time |
| `organizing-test-source-sets` | `test` vs `androidTest` placement |
| `understanding-the-testing-pyramid` | Prefer many small JVM tests over big device suites |
| `using-robolectric-correctly` | Host tests needing Android APIs (`Context`, DataStore file, `Log`, resources) |
| `.agents/skills/kotlin-coroutines-skill/` | Coroutine correctness in production *and* tests |

**Compose UI (when writing or expanding `androidTest`)**

| Skill | When |
|-------|------|
| `structuring-a-compose-test` | Class layout, `createComposeRule` |
| `configuring-test-dependencies` | Compose UI test Gradle deps |
| `setting-up-host-vs-device-tests` | JVM vs instrumented Compose choice |
| `finding-nodes-by-tag-text-content` | Finders |
| `asserting-node-state-and-text` | Assertions |
| `clicking-and-scrolling` | Basic actions |
| `synchronizing-with-idle` | `waitForIdle` / `waitUntil` |

Do **not** default to Mockito, Espresso-Views, UiAutomator, FragmentScenario, or ADB device-ops skills unless the task explicitly needs them. Prefer plain JVM tests + fakes; use **Robolectric** only when the class under test needs Android framework behavior (e.g. repository / DataStore tests).

## Guardrails

- Match neighboring code style; run ktlint — do not hand-reformat unrelated files.
- Do not add Room, network stacks, or multi-module Clean splits unless the task explicitly requires them.
- Do not put Compose / UI types in `domain/` (existing `Category.color: Color` is debt — do not spread it).
- Do not let `data/` import `presentation/` (existing leaks exist — do not add more).
- Domain APIs should not expose DataStore `Preferences` types to new code; prefer domain models / typed settings.
- Domain `use_cases/` is empty: ViewModels may call repositories directly. Add a use case only when logic is reused across ViewModels or is complex enough to extract — not as boilerplate.
- Timer control goes through the FGS / `ServiceHelper` path; do not reimplement countdown only in the ViewModel.
- Prefer extending existing features over scaffolding unfinished areas (`stats`, `taskdetail`, dummy tasks) unless the task is about those features.
- Prefer pointing at canonical files over copying large snippets into docs or comments.

## Skills and docs

- Compose: always use `compose-expert`
- Testing: see **Testing conventions** (skills tables above)
- Coroutines (production + tests): `.agents/skills/kotlin-coroutines-skill/`
- Human README: `README.md` (ktlint)
- CI: `.github/workflows/ci.yml`
